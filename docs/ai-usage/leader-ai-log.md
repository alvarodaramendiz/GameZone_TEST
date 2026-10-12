# AI Usage Log — Technical Lead

This document records the AI-assisted activities performed by the Technical Lead for the GameZone Unicesar project. It includes the architectural, documentation, and version-control work carried out during the project.

---

## Entry 1 — Layered Architecture Design

**Date:** 2026-09-02  
**Tool used:** Gemini

**Reason for use:**

Implementation of a layered architecture for the Java application.

**Problem faced:**

A readable package organization was necessary to distribute responsibilities among team members and maintain a clear separation between the domain model, business logic, persistence, and user interface.

**Prompt used:**

> Proporcioname una organización de paquetes implícita de acuerdo con una arquitectura de capas. Solamente necesito paquetes. Perfecto para Java

**Solution obtained and decision taken:**

The proposed architecture consisted of four conceptual layers:

- `domain`
- `application`
- `infrastructure`
- `presentation`

The Technical Lead adapted these names to the conventions of the project:

- `model` — Domain entities
- `logic` — Business logic and services
- `data` — Persistence components
- `ui` — User-interface components

The four-layer architecture was adopted as the structural foundation of the system.

---

## Entry 2 — Version-Control Documentation

**Date:** 2026-09-27  
**Tool used:** GitHub Copilot

**Reason for use:**

Create a version-control document that consolidated the repository's branching strategy, branches, commits, Pull Requests, branch protection rules, tags, and quantitative metrics.

**Problem faced:**

The project needed a source-of-truth document similar to a reference version-control report. The document had to reflect the actual repository state, including its Git history and GitHub activity.

**Prompt used:**

> I need a version control file, similar to this.

The prompt included a reference document with sections for:

- Branching strategy
- Branch history
- Commit log
- Commit attribution
- Pull Requests
- Branch protection
- Release tags
- Quantitative summaries

**Solution obtained and decision taken:**

A `VERSION_CONTROL.md` document was planned for the repository:

`alvarodaramendiz/Taller-GameZoneUnicesar-P3`

The document included the following sections:

- Branching strategy
- Repository details
- Pull Request history
- Team members and contributions
- Development phases
- Release history
- Key metrics
- Branch protection rules
- Commit strategy

The repository information used during the preparation included:

- Java as the primary language
- `main` as the default branch
- 20 merged Pull Requests
- Contributors including `alvarodaramendiz`, `jahdieldtorres`, `EstefaniaMarquez`, and `Sdfloria333`

---

## Entry 3 — Architectural Diagram Documentation

**Date:** 2026-09-27  
**Tool used:** GitHub Copilot

**Reason for use:**

Generate the architectural documentation required by the project in Markdown format using Mermaid diagrams.

**Problem faced:**

The repository needed three complementary documents to explain the system at different levels:

1. The dependency flow between architectural layers
2. The inheritance hierarchy of the domain classes
3. The complete class structure and relationships of the application

**Prompt used:**

> Generate the class-diagram.md, hierarchy-diagram.md and layer-diagram.md needed in the repository.

The prompt also supplied preliminary Mermaid content for the layer diagram and class hierarchy.

**Solution obtained and decision taken:**

The following documents were designed for the `docs/` directory:

- `docs/layer-diagram.md`
- `docs/hierarchy-diagram.md`
- `docs/class-diagram.md`

The diagrams were written using Mermaid syntax so that GitHub could render them natively.

The documents represented the following layers:

- Presentation layer
- Business logic layer
- Data or persistence layer
- Model layer

The class diagram included the principal project components:

- `MainFrame`
- `ProductPanel`
- `PersonPanel`
- `SalePanel`
- `ProductService`
- `CustomerService`
- `SellerService`
- `SaleService`
- `ProductPersistence`
- `CustomerPersistence`
- `SellerPersistence`
- `SalePersistence`
- `Person`
- `Customer`
- `Seller`
- `Product`
- `VideoGame`
- `Console`
- `Sale`
- `SaleItem`

---

## Entry 4 — Removal of the Return Module

**Date:** 2026-09-27  
**Tool used:** GitHub Copilot

**Reason for use:**

Correct the architectural diagrams so that they represented only the functionality currently included in the system.

**Problem faced:**

The first version of the diagrams included a `Return` module. However, that module does not belong to the current system scope and should not appear in the official architecture documentation.

**Prompt used:**

> From class-diagram; Remove return module. It does not belong to the system yet. Rename every -DAO to -Persistence

**Solution obtained and decision taken:**

The diagrams were revised to:

- Remove the `Return` class
- Remove return-related methods from `SaleService`
- Remove return-related methods from `SalePanel`
- Remove return relationships from the class diagram
- Remove return references from the hierarchy documentation
- Remove `Return.java` from the layer diagram

The current architecture documents only the implemented and approved system scope.

---

## Entry 5 — Persistence Naming Standardization

**Date:** 2026-09-27  
**Tool used:** GitHub Copilot

**Reason for use:**

Standardize the names of the persistence components across the architectural documentation.

**Problem faced:**

The initial diagrams used the `DAO` suffix for some data-layer classes, while the project documentation required the `Persistence` naming convention.

**Prompt used:**

> Rename every -DAO to -Persistence

**Solution obtained and decision taken:**

The following names were standardized:

- `ProductDAO` → `ProductPersistence`
- `CustomerDAO` → `CustomerPersistence`
- `SellerDAO` → `SellerPersistence`
- `SalePersistence` remained unchanged because it already followed the required convention

The diagrams now consistently use the `Persistence` suffix for data-layer components.

---

## Entry 6 — Technical Lead-Only AI Usage Log

**Date:** 2026-09-27  
**Tool used:** GitHub Copilot

**Reason for use:**

Create an AI usage log containing only the activities performed by the Technical Lead.

**Problem faced:**

An initial version of the AI usage log included activities attributed to other simulated developers. That did not correspond to the documentation requirement. The final log needed to document only the Technical Lead's own AI-assisted work.

**Prompt used:**

> The logs are only for me, the technical leader. I only need my AI use logs. Document the ones I did here and previous chats.

**Solution obtained and decision taken:**

The AI usage log was reduced to the Technical Lead's activities:

- Layered architecture design
- Version-control documentation planning
- Architectural diagram generation
- Removal of the Return module from the diagrams
- Renaming DAO classes to Persistence classes
- Preparation of this Technical Lead-only log

Entries attributed to Developer 1, Developer 2, or other team members were excluded.

---

## Summary

| Metric | Total |
|---|---:|
| Technical Lead AI-assisted activities documented | 6 |
| Gemini entries | 1 |
| GitHub Copilot entries | 5 |
| Other developers' entries included | 0 |
| Architectural diagram files planned | 3 |
| Layers documented | 4 |

### Main deliverables

- Four-layer Java architecture
- Version-control documentation structure
- Layer diagram
- Class hierarchy diagram
- Complete class diagram
- Removal of the unimplemented Return module
- Standardized `Persistence` naming convention
- Technical Lead-only AI usage log

---

*This document records AI assistance used by the Technical Lead. All final architectural and documentation decisions were reviewed and approved by the Technical Lead.*
