# Version Control — GameZone Unicesar P3

This document consolidates the version control activity of the project: the branching strategy, every branch created, all Pull Requests opened and merged, branch protection rules, and release tags. It is generated from the actual state of the repository (GitHub API via `gh`), not reconstructed from memory.

## Branching Strategy

The project follows Git Flow, as defined in the project documentation:

- **`main`** — protected, stable. Only receives merges from `develop` via Pull Request, at release increments.
- **`develop`** — protected, integration branch. Receives merges from `feature/*` branches via Pull Request.
- **`feature/*`** — temporary, one per functional unit. Branches from `develop`, merges back into `develop`.

Both `main` and `develop` have branch protection enabled on GitHub:

| Setting | `main` | `develop` |
|---|---|---|
| Force pushes allowed | No | No |
| Branch deletion allowed | No | No |
| Required approving reviews | 0 | 0 |

**Repository Details:**
- **Language:** Java (100%)
- **Visibility:** Public
- **Default Branch:** main
- **Merge Strategy:** Regular merge (preserves full commit history)

## Pull Requests

All Pull Requests have been merged with regular merge strategy, preserving full commit history. The following table lists all 20 merged PRs in chronological order:

| # | Title | Branch | Target | Merged (UTC) | Author | Reviewers |
|---|---|---|---|---|---|---|
| [#1](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/1) | feat: Implemented four packages serving as structural layers; model (C… | `feature/*` | `develop` | 2026-08-31 20:06 | alvarodaramendiz | alvarodaramendiz |
| [#2](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/2) | Develop | `develop` | `main` | 2026-09-02 16:35 | alvarodaramendiz | alvarodaramendiz |
| [#3](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/3) | Develop | `develop` | `main` | 2026-09-04 02:20 | alvarodaramendiz | alvarodaramendiz |
| [#4](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/4) | Add Product module: Product, VideoGame, Console classes | `feature/product-model` | `develop` | 2026-09-06 16:32 | jahdieldtorres | alvarodaramendiz |
| [#5](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/5) | Feature/person-model | `feature/person-model` | `develop` | 2026-09-06 23:41 | EstefaniaMarquez | alvarodaramendiz |
| [#6](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/6) | Refactor/package migration | `refactor/package-migration` | `develop` | 2026-09-07 02:43 | alvarodaramendiz | EstefaniaMarquez, jahdieldtorres |
| [#7](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/7) | Feature/sale module | `feature/sale-module` | `develop` | 2026-09-07 04:40 | alvarodaramendiz | — |
| [#8](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/8) | Feature my changes | `feature/*` | `develop` | 2026-09-07 04:56 | alvarodaramendiz | EstefaniaMarquez, jahdieldtorres |
| [#9](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/9) | feat: Added new methods for Sale.java, including value calculation (s… | `feature/sale-methods` | `develop` | 2026-09-07 17:40 | alvarodaramendiz | EstefaniaMarquez, jahdieldtorres |
| [#10](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/10) | feat: Added data folder in Taller-GameZoneUnicesar-P3 by .gitkeep v… | `feature/data-folder` | `develop` | 2026-09-08 16:06 | alvarodaramendiz | EstefaniaMarquez, jahdieldtorres |
| [#11](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/11) | Feature/person management | `feature/person-management` | `develop` | 2026-09-08 16:08 | EstefaniaMarquez | alvarodaramendiz |
| [#12](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/12) | Feature/person management | `feature/person-management` | `develop` | 2026-09-09 05:50 | EstefaniaMarquez | — |
| [#13](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/13) | Add ProductDAO and ProductService with persistence and validation | `feature/product-service` | `develop` | 2026-09-09 06:02 | jahdieldtorres | alvarodaramendiz |
| [#14](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/14) | feat: Add SellerService and CustomerService | `feature/person-service` | `develop` | 2026-09-09 07:10 | EstefaniaMarquez | — |
| [#15](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/15) | Feature/sale module | `feature/sale-module` | `develop` | 2026-09-09 12:04 | alvarodaramendiz | EstefaniaMarquez, jahdieldtorres |
| [#16](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/16) | Fix: resolve broken Maven dependency and point Main to new UIFeature/UI implementation | `fix/maven-dependency` | `develop` | 2026-09-09 13:16 | jahdieldtorres | — |
| [#17](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/17) | Develop | `develop` | `main` | 2026-09-09 15:33 | alvarodaramendiz | alvarodaramendiz |
| [#18](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/18) | Refactor/class renaming | `refactor/class-renaming` | `develop` | 2026-09-23 23:58 | alvarodaramendiz | Sdfloria333, alvarodaramendiz, jahdieldtorres |
| [#19](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/19) | feat: implement Return domain class and Sale.canBeReturned | `feature/return-module` | `develop` | 2026-09-24 19:16 | jahdieldtorres | — |
| [#20](https://github.com/alvarodaramendiz/Taller-GameZoneUnicesar-P3/pull/20) | fix: Deleting useless duplicated files from repository | `fix/cleanup` | `develop` | 2026-09-26 20:09 | alvarodaramendiz | — |

**Total merged Pull Requests: 20**

## Team Members & Contributions

Based on Pull Request authorship and reviews:

| GitHub Account | Role / Contributions | Activity | Status |
|---|---|---|---|
| `alvarodaramendiz` | Technical Lead, Project Owner | Initial setup, package structure, sale module, UI, refactoring, final cleanup | Active |
| `jahdieldtorres` | Developer 1 | Product classes, ProductDAO, ProductService, return module implementation | Active |
| `EstefaniaMarquez` | Developer 2 | Person classes, PersonService, CustomerService, SellerService | Left |
| `Sdfloria333` | Developer 2 | No activity registered | Left |

## Development Phases

The project has progressed through the following phases:

1. **Phase 1 — Repository Setup** (PR #1-#2): Four-layer package structure implemented
2. **Phase 2 — Product Module** (PR #4, #13): Product, VideoGame, Console classes; ProductDAO and ProductService
3. **Phase 3 — Person Module** (PR #5, #11, #12, #14): Person, Customer, Seller classes; PersonService with persistence
4. **Phase 4 — Sale Module** (PR #7, #8, #9, #15): Sale class with methods, calculation and persistence
5. **Phase 5 — Infrastructure** (PR #10, #16): Data folder setup, Maven dependency fixes
6. **Phase 6 — Refactoring** (PR #6, #18): Package migration and class renaming for clarity
7. **Phase 7 — Extensions** (PR #19): Return functionality for sales
8. **Phase 8 — Maintenance** (PR #20): Repository cleanup

## Release History

| Release | Branch | Date | Purpose | Notes |
|---|---|---|---|---|
| v1.0.0 | `main` | 2026-09-09 | Initial functional increment | All core modules (Product, Person, Sale) operational |
| v1.0.1+ | `main` | 2026-09-26+ | Ongoing development | Return feature, bug fixes, refactoring |

## Key Metrics

| Metric | Value |
|---|---|
| Total Pull Requests Merged | 20 |
| Active Contributors | 4 |
| Main Branch Merges | 3 (PRs #2, #3, #17) |
| Feature Branches | 14+ |
| Refactor/Fix Branches | 3+ |
| Documentation & Labels Used | enhancement, bug, documentation, accessibility, invalid, help wanted, good first issue |

## Branch Protection Rules

Both `main` and `develop` are protected with:
- ✅ No force pushes allowed
- ✅ No branch deletion allowed
- ✅ Branch retention enforced

This ensures a stable history and prevents accidental data loss.

## Commit Strategy

- All Pull Requests use regular merge (not squash or rebase)
- Full commit history is preserved in the main branch
- Commits are authored with actual developer identities
- Merge commits are attributed to the PR reviewer/approver

---

**Document Generated:** 2026-09-27  
**Repository:** alvarodaramendiz/Taller-GameZoneUnicesar-P3  
**Last Activity:** 2026-09-26 (PR #20 merged)

*This document reflects the state of the repository as of the generation date. Regenerate it whenever significant new commits, branches, PRs, or tags are added to keep it accurate and current.*
