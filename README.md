# Job Portal — MCA Resume Project (Java & SQL Backend)

An AI-Powered Job Portal & Resume Matcher web application built as a Master of Computer Applications (MCA) resume project.

## 🚀 Tech Stack
- **Backend**: Java 21 (`com.sun.net.httpserver.HttpServer`)
- **Database**: SQL / MySQL (`schema.sql`)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla SPA)
- **AI Engine**: Java Resume Matcher & ATS Scoring Module

## 📂 Project Structure
```
job-portal/
├── schema.sql              # MySQL / SQL Database schema & seed data
├── build_and_run.bat      # One-click Java compile & start script
├── .gitignore
├── src/
│   └── com/jobportal/
│       ├── Main.java       # Java HttpServer entry point
│       ├── model/          # Java data models (Job, Application, MatchResult)
│       ├── db/             # Java Database layer
│       ├── ai/             # Java AI Resume Analysis engine
│       └── handler/        # REST API & Static handlers
└── web/
    ├── index.html          # SPA view (Student, Recruiter, Admin)
    ├── css/style.css       # Glassmorphism dark mode design system
    └── js/app.js           # SPA frontend logic
```

## 🛠️ Quick Start
Run `build_and_run.bat` or compile with Java:
```cmd
javac -d bin -sourcepath src src/com/jobportal/model/*.java src/com/jobportal/ai/*.java src/com/jobportal/db/*.java src/com/jobportal/handler/*.java src/com/jobportal/Main.java
java -cp bin com.jobportal.Main 8080
```
Then visit `http://localhost:8080` in your browser.
