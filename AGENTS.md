# CLAUDE.md

# Guidelines

## Language

- Respond to users in Japanese.
- Write documentation and comments in Japanese.

## Coding Principles

- Keep the level of abstraction consistent within each method. Avoid exposing low-level implementation details in only part of a method while the rest remains high-level.
- Separate side effects from pure logic. Prefer pure functions where possible to keep the code easy to test.
- Write tests for the implementation.
- Organize code files around meaningful units of responsibility.
- Document any high-level context that cannot be understood from the code alone.

## Environment

- Manage libraries and modules using an appropriate package manager.
- Prefer immutable and reproducible environments.
  - Use lock files when the package manager supports them.
  - Pin dependencies by commit hash when such a mechanism is available.

### Language-Specific Requirements

#### TypeScript / JavaScript

- Use pnpm.
- Use Biome as the linter and formatter.

#### Python

- Use uv.