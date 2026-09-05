# ProtoFlow

ProtoFlow is a desktop application developed for the **Programming and Development Paradigms (PPS)** course at the University of Bologna.

The project implements a document protocol management system that supports the complete lifecycle of a document, from loading and registration to archiving.

The application provides role-based functionalities for administrators, operators and viewers, including document management, account management, logging, search, statistics and PDF report generation.

## Authors

- Francesco De Aloysio
- Roberto Pisu
- Thomas Testa

## Main Features

ProtoFlow provides:

- user authentication and role-based authorization;
- registration request management;
- account, role and classification management;
- document loading, registration and archiving;
- document and operation logging;
- document and log search/filtering;
- management dashboards and statistics;
- PDF generation, visualization and printing;
- Prolog-based authorization rules.

The application defines three main roles:

- **Admin**: system administration and complete access to management functionality;
- **Oper**: document loading, registration and archiving;
- **Viewer**: document search and consultation with restricted visibility.

## Technologies

The project is mainly developed with:

- **Scala 3.3.7**
- **ScalaFX 21.0.0-R32**
- **Java / JDK 21**
- **sbt 1.12.3**
- **tuProlog 3.3.0**
- **Apache PDFBox 2.0.30**
- **Scala XML 2.4.0**
- **JUnit 4.13.2**

Data persistence is implemented using XML files.

## Requirements

To build and run the project, install:

- JDK 21
- sbt
- Git

An Internet connection is required the first time the project is built in order to download its dependencies.

Verify the installation with:

```bash
java -version
sbt --version
```

The recommended Java version is **Java 21**, which is also used by the Continuous Integration workflow.

## Getting Started

Clone the repository:

```bash
git clone https://github.com/francesco-dealoysio/PPS-25-ProtoFlow.git
cd PPS-25-ProtoFlow
```

The following commands must be executed from the project root, where `build.sbt` is located.

Clean and compile the project:

```bash
sbt clean compile
```

Run the application:

```bash
sbt run
```

At startup, ProtoFlow automatically initializes the required directory structure and configuration under the `protoflow/` directory.

## Running Tests

Before running the test suite on a freshly cloned repository, initialize the ProtoFlow environment:

```bash
sbt "runMain pkg.b.logic.tryInit"
```

Then execute:

```bash
sbt test
```

Parallel test execution is disabled because some tests operate on shared XML resources.

## Demo Accounts

The initial dataset provides the following accounts:

| Role | Username | Password |
|------|----------|----------|
| Admin | `frank` | `topolino` |
| Oper | `tommy` | `tommy$123` |
| Viewer | `robby` | `robby$456` |

These credentials refer to the initial application data.

## Project Structure

```text
src/
├── main/
│   ├── scala/
│   │   └── pkg/
│   │       ├── a/    GUI, navigation, services and validation
│   │       ├── b/    Domain and application logic
│   │       ├── c/    Data access and XML persistence
│   │       └── d/    Utilities
│   └── resources/
│       ├── prolog/
│       ├── img/
│       └── *.css
└── test/
    └── scala/

protoflow/
├── database/
├── ids/
├── log/
├── prints/
├── test/
└── docs/
```

The main application entry point is:

```text
pkg.RunApp
```

## Application Data

ProtoFlow stores its runtime data inside the `protoflow/` directory.

At startup, the application generates `protoflow/protoflow.properties` using paths relative to the current project directory.

For this reason, the application should normally be launched from the repository root.

Generated PDF reports are stored in:

```text
protoflow/prints/
```

## Documentation

Project documentation is available under:

```text
protoflow/docs/
```

The final project report starts from:

```text
protoflow/docs/releases/relazione/0-Indice.md
```

It contains the development process, requirements, architectural design, detailed design, implementation, testing and project retrospective.

## Continuous Integration

A GitHub Actions workflow automatically runs the test suite on pushes to the `develop` branch.

The CI environment uses:

```text
Ubuntu
Java 21
sbt
```

The workflow performs:

```bash
sbt clean
sbt "runMain pkg.b.logic.tryInit"
sbt test
```

## License

This project was developed for academic purposes as part of the Programming and Development Paradigms course at the University of Bologna.