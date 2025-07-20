# 🚀 CodeTrack: Mini Coding Assessment Portal

[![Java](https://img.shields.io/badge/Java-11%2B-blue?logo=java)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-Embedded%20DB-lightgrey?logo=sqlite)](https://www.sqlite.org/)
[![Build With Maven](https://img.shields.io/badge/Built%20with-Maven-cc71c6?logo=apachemaven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> A lightweight, educational Java Swing application to simulate a mini LeetCode-style coding test system. Great for beginners exploring Java GUI, backend services, and database integration — all in one place.

---

## 📌 Overview

**CodeTrack** is a desktop-based mini coding assessment platform that enables users to:

- Add and solve coding problems
- Evaluate pre-defined solutions
- View submission history with results like Accepted, TLE, etc.
- Gain insights into execution time and logic performance

It showcases **Layered Architecture**, **functional interfaces**, and **object-oriented principles**, making it a solid Java learning project.

---

## 🧩 System Architecture

```
                   ┌────────────────────────────┐
                   │       CodeTrackGUI         │
                   │    (Main JFrame & Panels)  │
                   └────────────┬───────────────┘
                                │
                ┌───────────────▼─────────────────┐
                │         GUI Components          │
                │ HomePanel, ProblemListPanel,    │
                │ AddProblemPanel, etc.           │
                └───────────────┬─────────────────┘
                                │
                ┌───────────────▼──────────────────┐
                │         Service Layer            │
                │ ProblemService, SubmissionService│
                │ JudgeService                     │
                └───────────────┬──────────────────┘
                                │
                ┌───────────────▼────────────┐
                │       DAO (Data Access)    │
                │ ProblemDAO, SubmissionDAO  │
                └───────────────┬────────────┘
                                │
                    ┌───────────▼───────────┐
                    │     SQLite (DB)       │
                    │   codetrack.db file   │
                    └───────────────────────┘
```

---

## ✨ Highlight Features

### 🧠 Problem Management

- Add new problems with rich details and multiple test cases.
- Automatically load default problems from `problems.json` at first run.
- View existing problems in a list format.

### ⚙️ Code Judging

- Built-in `JudgeService` simulates backend judging.
- Functional interface `SolutionBank` lets you plug in problem-specific Java classes.
- Outputs are judged based on:
  - ✅ Correctness
  - ❌ Wrong Answer
  - ⏱ Time Limit Exceeded (default 2s)
  - 💥 Runtime Error

### 📈 Submission Tracking

- Each submission is recorded with:
  - Problem ID
  - Result
  - Time taken
  - Timestamp

### 💾 Persistent Storage

- Uses SQLite for offline, local database storage.
- Tables: `problems`, `submissions`
- Automatically initialized if not present.

### 🧰 Utility Classes

- `TimerUtil`: For benchmarking solution execution time.
- JSON conversion using Jackson's `ObjectMapper`.

---

## 🧪 Sample Test Case Format

```json
[
  {
    "input": "5",
    "expectedOutput": "25",
    "description": "Square of 5"
  },
  {
    "input": "2",
    "expectedOutput": "4"
  }
]
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/codetrack/
│   │   ├── dao/             # Data access logic (ProblemDAO, SubmissionDAO)
│   │   ├── gui/             # Swing-based GUI (panels, main frame)
│   │   ├── model/           # Domain models (Problem, Submission, TestCase)
│   │   ├── service/         # Judging & problem mgmt logic
│   │   ├── solutionbank/    # Interfaces + problem implementations
│   │   └── util/            # Helpers (TimerUtil, DB manager)
│   └── resources/
│       └── problems.json    # Seed data for problems
└── pom.xml                  # Maven configuration
```

---

## 💡 Future Enhancements

- 🖊 **Custom code input** field with syntax highlighting (via RSyntaxTextArea)
- 🧑‍💻 **User-authentication system**
- ⬆️ **Update/Delete** from GUI
- 📤 **Import/export problems** via JSON/CSV
- 📊 **Statistics dashboard** for problem difficulty, average runtime, etc.
- 🌐 **Web version** using Spring Boot + Thymeleaf/React

---

## 🏁 Setup & Run

### Prerequisites

- JDK 11+
- Apache Maven

### Install & Run

```bash
git clone https://github.com/ezanand/codetrack.git
cd codetrack
mvn clean install
java -jar target/CodeTrack-1.0-SNAPSHOT.jar
```

On first launch:

- `codetrack.db` is auto-created.
- Default problems are seeded from `problems.json`.

---

## 🧾 License

This project is licensed under the [MIT License](LICENSE).

---

## 👨‍💻 Author

Built with ❤️ by **[Your Name]**  
Inspired by platforms like **LeetCode**, **HackerRank**, and **Codeforces**

> _"Practice like you’ve never won. Perform like you’ve never lost."_
