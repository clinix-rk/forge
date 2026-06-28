# Clinix Forge: Project Guidelines

These development rules are to be followed strictly during all coding and modeling tasks in the Clinix Forge repository.

## Spring Boot & JPA Best Practices
- **Self-Documenting Code**: Code must be clear, clean, and self-documenting. Use descriptive class, method, and variable names.
- **Documentation Comments Only**: Comments must be used strictly to document architecture, APIs, or explain *why* a particular implementation logic was chosen, rather than *what* the line of code does.
- **Feature-Wise Architecture**: Organize all classes (entities, controllers, services, mappers, repositories) by domain feature/module.
- **Proper Structure**: Ensure code is structured cleanly into logical classes and reusable methods.
- **Consistency**: Keep the codebase cohesive. Always review the existing architecture and structure new code so that it integrates seamlessly.
- **Code Implementation**: Never fight with the framework, Find ways within the framework to implement the required functionality. Always follow Spring Boot and JPA best practices for implementation.

## Testing Guidelines
- **Write Tests**: Always write unit and integration tests where required.
- **JUnit 5**: Create a full test suite for the code you are writing using JUnit 5.
- **Verification**: Always run and verify all tests before completing a task.
- **Ambiguity**: If any situation leads to ambiguity for a test result, always ask the user for clarification.

## Workflow & Communication Rules
- **Implementation Plan First**: Always create a detailed implementation plan (`implementation_plan.md`) and get it reviewed and approved by the user before writing or generating any code.
- **Clarification**: If any ambiguity is encountered, stop and ask questions to clarify.
- **Context Updates**: Update context files, project indexes, or the ruleset when required after executing a prompt.
