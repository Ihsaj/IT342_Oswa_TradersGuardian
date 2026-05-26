# IT342_G6_Oswa_Lab1

## Build and Run

### Java backend
- From the repository root:
  - `./mvnw.cmd -DskipTests package`
- Or from `backend/`:
  - `./mvnw.cmd -DskipTests package`

### Web frontend
- From `web/frontend/`:
  - `cmd /c "npm install --ignore-scripts"`
  - `cmd /c "npm run build"`

### Android mobile
- Create `mobile/local.properties` from `mobile/local.properties.example`.
- Set `sdk.dir` to your Android SDK path.
- Then run:
  - `./gradlew.bat build`

## Notes
- `mobile/local.properties` is ignored by Git and should not be committed.
- The web frontend build passed successfully.
- The backend Maven build passed successfully.
