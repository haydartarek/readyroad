package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.domain.repository.SignImportRunRepository;
import com.readyroad.readyroadbackend.storage.MediaUrlResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SignQuestionSourceEditTest {
    @TempDir Path sourceRoot;
    private final ObjectMapper mapper = new ObjectMapper();

    private SignQuizImportService service(RoadSignRepository signs, SignQuestionRepository questions) {
        PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
        when(transactions.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        SignQuizImportService service = new SignQuizImportService(signs, questions,
                mock(SignExamRepository.class), mock(SignImportRunRepository.class), mapper,
                transactions, mock(CanonicalSignCatalogService.class),
                mock(RoadSignReferenceTextResolver.class), mock(MediaUrlResolver.class));
        ReflectionTestUtils.setField(service, "signsImportPath", sourceRoot.toString());
        return service;
    }

    private Path copySource() throws Exception {
        Path directory = Files.createDirectory(sourceRoot.resolve("A7a"));
        Path fixture = Path.of("src/main/resources/data/signs_import/A7a");
        for (String name : new String[]{"sign.json", "questions.json", "exams.json"}) {
            Files.copy(fixture.resolve(name), directory.resolve(name));
        }
        return directory.resolve("questions.json");
    }

    @Test
    void editUpdatesCanonicalFileAndImportsOnlyThatSignsQuestions() throws Exception {
        Path source = copySource();
        RoadSignRepository signs = mock(RoadSignRepository.class);
        SignQuestionRepository questions = mock(SignQuestionRepository.class);
        when(signs.findFirstActiveBySignCodeCaseSensitive("A7a")).thenReturn(Optional.of(new RoadSign()));
        when(questions.findByQuestionRef(anyString())).thenReturn(Optional.empty());
        when(questions.save(any(SignQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SignQuizImportService service = service(signs, questions);

        var current = service.getSourceQuestion("A7a", "A7a_Q01");
        ObjectNode changed = (ObjectNode) current.question().deepCopy();
        ((ObjectNode) changed.path("i18n").path("EN")).put("explanation", "Updated road narrowing explanation.");
        var result = service.updateSourceQuestion("A7a", "A7a_Q01",
                new SignQuizImportService.SourceQuestion(changed, current.revision()));

        assertNotEquals(current.revision(), result.revision());
        assertEquals("Updated road narrowing explanation.",
                mapper.readTree(source.toFile()).get(0).path("i18n").path("EN").path("explanation").asText());
        verify(questions, atLeastOnce()).save(any(SignQuestion.class));
    }

    @Test
    void staleRevisionCannotOverwriteCanonicalSource() throws Exception {
        Path source = copySource();
        byte[] before = Files.readAllBytes(source);
        SignQuizImportService service = service(mock(RoadSignRepository.class), mock(SignQuestionRepository.class));
        var current = service.getSourceQuestion("A7a", "A7a_Q01");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateSourceQuestion("A7a", "A7a_Q01",
                        new SignQuizImportService.SourceQuestion(current.question(), "stale")));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertArrayEquals(before, Files.readAllBytes(source));
    }
}
