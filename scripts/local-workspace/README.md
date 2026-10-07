# Local workspace launcher

These files preserve the approved launcher and Compose configuration used at
the workspace root, alongside `readyroad` and `readyroad_front_end`.

To recreate that layout, copy `run-local.ps1` and `docker-compose.local.yml` into
the parent directory of both repositories. Keep the existing private backend
`.env`; credentials are not included here.

Run from Windows PowerShell:

```powershell
& "C:\Users\haydar\Desktop\end_project\run-local.ps1" -NoMobile
```

The release excludes Mobile and does not replace production configuration or
production data with local values.
