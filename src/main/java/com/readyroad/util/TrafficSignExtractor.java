package com.readyroad.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * مستخرج بيانات العلامات المرورية البلجيكية
 * Belgian Traffic Signs Data Extractor
 */
public class TrafficSignExtractor {

    private static final Map<String, CategoryInfo> CATEGORIES = new HashMap<>();
    
    static {
        CATEGORIES.put("A-serie", new CategoryInfo("علامات الخطر", "Danger Signs", "Gevaar", "Danger"));
        CATEGORIES.put("B-serie", new CategoryInfo("علامات الأولوية", "Priority Signs", "Voorrang", "Priorité"));
        CATEGORIES.put("C-serie", new CategoryInfo("علامات المنع", "Prohibition Signs", "Verbod", "Interdiction"));
        CATEGORIES.put("D-serie", new CategoryInfo("علامات الإلزام", "Mandatory Signs", "Gebod", "Obligation"));
        CATEGORIES.put("E-serie", new CategoryInfo("علامات الوقوف والانتظار", "Parking Signs", "Stilstaan en parkeren", "Stationnement"));
        CATEGORIES.put("F-serie", new CategoryInfo("علامات إرشادية", "Information Signs", "Aanwijzing", "Indication"));
        CATEGORIES.put("G-serie", new CategoryInfo("لوحات إضافية", "Additional Panels", "Onderborden", "Panneaux additionnels"));
        CATEGORIES.put("M-serie", new CategoryInfo("لوحات الدراجات", "Bicycle Signs", "Onderborden betreffende fietsen en bromfietsen", "Panneaux vélos"));
        CATEGORIES.put("T-serie", new CategoryInfo("علامات التحديد", "Boundary Signs", "Afbakeningsborden", "Balises"));
        CATEGORIES.put("Z-serie", new CategoryInfo("علامات المناطق", "Zone Signs", "Zoneborden", "Panneaux de zone"));
    }
    
    private static final Map<String, String> FOLDER_MAPPING = new HashMap<>();
    
    static {
        FOLDER_MAPPING.put("A-serie", "danger_signs");
        FOLDER_MAPPING.put("B-serie", "priority_signs");
        FOLDER_MAPPING.put("C-serie", "prohibition_signs");
        FOLDER_MAPPING.put("D-serie", "mandatory_signs");
        FOLDER_MAPPING.put("E-serie", "parking_signs");
        FOLDER_MAPPING.put("F-serie", "information_signs");
        FOLDER_MAPPING.put("G-serie", "additional_panels");
        FOLDER_MAPPING.put("M-serie", "bicycle_signs");
        FOLDER_MAPPING.put("T-serie", "boundary_signs");
        FOLDER_MAPPING.put("Z-serie", "zone_signs");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("🚦 بدء استخراج بيانات العلامات المرورية...");
        
        // قراءة ملف HTML
        String htmlFile = "data/traffic_signs.html";
        File file = new File(htmlFile);
        
        if (!file.exists()) {
            System.out.println("❌ الملف " + htmlFile + " غير موجود!");
            return;
        }
        
        // تحليل HTML
        Document doc = Jsoup.parse(file, "UTF-8");
        List<TrafficSign> signs = new ArrayList<>();
        
        String currentCategory = null;
        
        // البحث عن جميع الفئات
        Elements categoryHeaders = doc.select("h2");
        for (Element header : categoryHeaders) {
            String headerText = header.text();
            
            // تحديد الفئة
            for (String categoryKey : CATEGORIES.keySet()) {
                if (headerText.contains(categoryKey)) {
                    currentCategory = categoryKey;
                    
                    // البحث عن جميع العلامات في هذه الفئة
                    Element parent = header.parent();
                    if (parent != null) {
                        Elements listingDivs = parent.select("div.listing");
                        if (!listingDivs.isEmpty()) {
                            Element listing = listingDivs.first();
                            Elements items = listing.select("a.listing-item");
                            
                            for (Element item : items) {
                                TrafficSign sign = extractSign(item, currentCategory);
                                if (sign != null) {
                                    signs.add(sign);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        
        System.out.println("\n✓ تم استخراج " + signs.size() + " علامة مرورية");
        
        // إحصائيات
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (TrafficSign sign : signs) {
            categoryCounts.merge(sign.getCategoryCode(), Integer.valueOf(1), (a, b) -> Integer.valueOf(a.intValue() + b.intValue()));
        }
        
        System.out.println("\n📊 إحصائيات حسب الفئة:");
        categoryCounts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                CategoryInfo info = CATEGORIES.get(entry.getKey());
                String nameAr = info != null ? info.getNameAr() : entry.getKey();
                System.out.println("  " + entry.getKey() + " (" + nameAr + "): " + entry.getValue() + " علامة");
            });
        
        // حفظ في JSON
        ObjectMapper mapper = new ObjectMapper();
        String outputFile = "data/traffic_signs_data.json";
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), signs);
        
        System.out.println("\n✓ تم حفظ البيانات في: " + outputFile);
        
        // إنشاء هيكل المجلدات
        String baseImagePath = "mobile_app/assets/traffic_signs";
        createFolderStructure(baseImagePath);
        System.out.println("\n✓ تم إنشاء هيكل المجلدات في: " + baseImagePath);
        
        // السؤال عن تنزيل الصور
        System.out.print("\n❓ هل تريد تنزيل الصور الآن؟ (y/n): ");
        String answer;
        try (Scanner scanner = new Scanner(System.in)) {
            answer = scanner.nextLine().trim().toLowerCase();
        }
        
        if (answer.equals("y")) {
            System.out.println("\n📥 بدء تنزيل الصور...");
            int successful = 0;
            int failed = 0;
            
            for (int i = 0; i < signs.size(); i++) {
                TrafficSign sign = signs.get(i);
                if (sign.getImageUrl() == null || sign.getImageUrl().isEmpty()) {
                    continue;
                }
                
                String categoryCode = sign.getCategoryCode();
                String folderName = FOLDER_MAPPING.getOrDefault(categoryCode, "other");
                String signCode = sign.getSignCode() != null ? sign.getSignCode() : "sign_" + (i + 1);
                
                // تنظيف اسم الملف
                String cleanCode = signCode.replaceAll("[^\\w\\-]", "_");
                String imageFilename = cleanCode + ".png";
                String imagePath = baseImagePath + "/" + folderName + "/" + imageFilename;
                
                if (downloadImage(sign.getImageUrl(), imagePath)) {
                    sign.setImagePath("assets/traffic_signs/" + folderName + "/" + imageFilename);
                    successful++;
                    System.out.println("✓ تم تنزيل: " + imageFilename);
                } else {
                    failed++;
                    System.out.println("✗ فشل: " + imageFilename);
                }
                
                // توقف قصير
                Thread.sleep(500);
            }
            
            System.out.println("\n✅ اكتمل التنزيل: " + successful + " نجح، " + failed + " فشل");
            
            // حفظ البيانات المحدثة
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), signs);
            System.out.println("✓ تم تحديث البيانات مع مسارات الصور");
        }
        
        System.out.println("\n🎉 انتهت عملية الاستخراج بنجاح!");
        System.out.println("📁 الملفات المنتجة:");
        System.out.println("  - " + outputFile);
        System.out.println("  - " + baseImagePath + "/");
    }
    
    private static TrafficSign extractSign(Element item, String categoryCode) {
        TrafficSign sign = new TrafficSign();
        sign.setCategoryCode(categoryCode);
        
        CategoryInfo catInfo = CATEGORIES.get(categoryCode);
        if (catInfo != null) {
            sign.setCategoryNameAr(catInfo.getNameAr());
            sign.setCategoryNameEn(catInfo.getNameEn());
            sign.setCategoryNameNl(catInfo.getNameNl());
            sign.setCategoryNameFr(catInfo.getNameFr());
        }
        
        // استخراج الرابط
        sign.setUrl(item.attr("href"));
        
        // استخراج الصورة
        Elements imgs = item.select("img");
        if (!imgs.isEmpty()) {
            Element img = imgs.first();
            String srcset = img.attr("srcset");
            String src = img.attr("src");
            
            if (!srcset.isEmpty()) {
                // البحث عن رابط 2x
                String[] parts = srcset.split(",");
                for (String part : parts) {
                    if (part.contains("2x")) {
                        sign.setImageUrl(part.trim().split("\\s+")[0]);
                        break;
                    }
                }
                if (sign.getImageUrl() == null && parts.length > 0) {
                    sign.setImageUrl(parts[0].trim().split("\\s+")[0]);
                }
            } else if (!src.isEmpty()) {
                sign.setImageUrl(src);
            }
            
            sign.setAlt(img.attr("alt"));
        }
        
        // استخراج العنوان
        Elements titles = item.select("div.listing-item__title");
        if (!titles.isEmpty()) {
            String titleNl = titles.first().text().trim();
            sign.setTitleNl(titleNl);
            
            // استخراج رمز العلامة
            Pattern pattern = Pattern.compile("^([A-Z]+\\d+[a-zA-Z]*)\\s+(.+)$");
            Matcher matcher = pattern.matcher(titleNl);
            if (matcher.matches()) {
                sign.setSignCode(matcher.group(1));
                sign.setDescriptionNl(matcher.group(2));
            } else {
                sign.setDescriptionNl(titleNl);
            }
        }
        
        return sign.getTitleNl() != null ? sign : null;
    }
    
    private static void createFolderStructure(String basePath) throws IOException {
        for (String folderName : FOLDER_MAPPING.values()) {
            Path path = Paths.get(basePath, folderName);
            Files.createDirectories(path);
        }
    }
    
    private static boolean downloadImage(String urlString, String savePath) {
        try {
            java.net.URI uri = java.net.URI.create(urlString);
            URL url = uri.toURL();
            Path path = Paths.get(savePath);
            Files.createDirectories(path.getParent());
            
            try (InputStream in = url.openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Inner classes
    static class CategoryInfo {
        private String nameAr;
        private String nameEn;
        private String nameNl;
        private String nameFr;
        
        public CategoryInfo(String nameAr, String nameEn, String nameNl, String nameFr) {
            this.nameAr = nameAr;
            this.nameEn = nameEn;
            this.nameNl = nameNl;
            this.nameFr = nameFr;
        }
        
        public String getNameAr() { return nameAr; }
        public String getNameEn() { return nameEn; }
        public String getNameNl() { return nameNl; }
        public String getNameFr() { return nameFr; }
    }
    
    static class TrafficSign {
        private String signCode;
        private String categoryCode;
        private String categoryNameAr;
        private String categoryNameEn;
        private String categoryNameNl;
        private String categoryNameFr;
        private String titleNl;
        private String descriptionNl;
        private String imageUrl;
        private String imagePath;
        private String url;
        private String alt;
        
        // Getters and setters
        public String getSignCode() { return signCode; }
        public void setSignCode(String signCode) { this.signCode = signCode; }
        
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        
        public String getCategoryNameAr() { return categoryNameAr; }
        public void setCategoryNameAr(String categoryNameAr) { this.categoryNameAr = categoryNameAr; }
        
        public String getCategoryNameEn() { return categoryNameEn; }
        public void setCategoryNameEn(String categoryNameEn) { this.categoryNameEn = categoryNameEn; }
        
        public String getCategoryNameNl() { return categoryNameNl; }
        public void setCategoryNameNl(String categoryNameNl) { this.categoryNameNl = categoryNameNl; }
        
        public String getCategoryNameFr() { return categoryNameFr; }
        public void setCategoryNameFr(String categoryNameFr) { this.categoryNameFr = categoryNameFr; }
        
        public String getTitleNl() { return titleNl; }
        public void setTitleNl(String titleNl) { this.titleNl = titleNl; }
        
        public String getDescriptionNl() { return descriptionNl; }
        public void setDescriptionNl(String descriptionNl) { this.descriptionNl = descriptionNl; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getAlt() { return alt; }
        public void setAlt(String alt) { this.alt = alt; }
    }
}
