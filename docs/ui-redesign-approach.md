# Payara Starter UI redesign: recommended approach

| | |
| --- | --- |
| Ticket | FISH-14046 |
| Pull request | [#191](https://github.com/payara/ecosystem-starter/pull/191) |
| Status | **Proposed**, waiting for approval |
| Approved by | _(name, date)_ |

This document records how the Payara Starter page (`starter-ui/src/main/webapp`) is being redesigned and why, so the approach can be approved before further work builds on it.

## Problem

The page ran on an old theme (`ui/js/interface.min.js` with jQuery, jquery-editable-select and its own CSS, fonts and images) that looks unrelated to Payara's newer products. A review against common usability heuristics (NN/g) also found:

- **Too many steps for the content.** 8 steps, several with one or two controls.
- **Related options on different steps.** Gradle disables Include Tests 3 steps later; Jakarta EE and Payara versions narrow the Java versions on another step; Core Profile turns off Security and the ER Diagram Designer without saying so where it is chosen.
- **A dependent-option bug.** Choosing Jakarta EE 8, 9 or 9.1 with Core Profile selected switched to Web Profile but left Security and the ER Diagram Designer disabled.
- **No review before generating and no field checks.** A Group ID with a space, or a package such as `com.new.app`, went straight to the generator.
- **Weak feedback when generating.** A line of grey text, and errors shown with `alert()`.
- **HubSpot marketing pop-ups** over the form while it is being filled in.
- **Accessibility.** axe-core reported a serious `aria-hidden-focus` violation; option groups had no accessible name; notices appeared silently.

## Recommendation

1. **Now (interim): plain HTML, CSS and JavaScript on the payara-ds design tokens.**
   - Styles use only payara-ds tokens (`ui-ds/tokens.css`); component styles follow the payara-ds React components (Button, Input, Select, Combobox, Tabs, Tooltip, Modal, Checkbox), so the page already looks like the target.
   - No build step: the page stays static files served by the WAR, as before.
   - jQuery, jquery-editable-select and the old theme are removed.
2. **Later: move to the observability-tool React components** once their first package is published. Until then there is nothing to depend on, and a React toolchain now would mean building the page twice.

### Why not React now

- The React components package is not published yet.
- `starter-ui` has no JavaScript build. Adding one (Node, bundler, Maven integration) is its own piece of work and is only worth doing for the real components.
- The interim page follows the components' tokens and states, so the move should not change how the page looks.

## What changes for users

- **4 steps** instead of 8: Project, Platform, Features, Review. Options that depend on each other are on the same step.
- **Review step** listing every choice, with Edit back to each step.
- **Field checks** for Group ID, Artifact ID, Version, Package and the ER subpackages.
- **Result screen** while generating: ready (Download again, Back to options, Start over) or failed (Back to options, Try again), in place of `alert()`.
- **ER Diagram Builder** in a native dialog: searchable picker, Code and Preview tabs, zoom, version history, prompt composer.
- **HubSpot marketing pop-ups hidden.** The cookie banner, analytics and Privacy Settings are unchanged.
- **Accessibility fixes**: named groups and fields, focus management, announced notices, keyboard support. axe-core 3.5.6 and 4.12 report no violations on any step (as tested for #191).

## What does not change

- **The backend.** No Java, resource or POM changes.
- **The generated projects.** For the same choices the page sends `resources/starter` the same request as before, and the defaults (including Docker, unchecked) are the same.

## Themes

Only the light theme (`enterpriseLight`) is included.

Because `starter.css` uses only tokens, more themes do not need new styles. Adding them means:
- loading the payara-ds theme bundle (`themes.css`, all 5 themes) instead of the single light theme;
- a way to pick the theme (a switcher, or following the system setting);
- checking every screen in each theme, including the parts not drawn with tokens: the hero image (`ui-ds/hero-azul.svg`), the CodeMirror editor and the Mermaid preview.

Recommended as a separate task, so this change stays a redesign of the existing page.

`ui-ds/tokens.css` is a copy of the payara-ds light theme for now. It will be taken from the payara-ds repository directly (separate task).

## Third-party code and assets

| What | Where | Licence |
| --- | --- | --- |
| CodeMirror 5.63.1 | cdnjs, with SRI | MIT |
| Mermaid 10.9.8 | jsDelivr, version pinned; no SRI, since its ES module build loads its own chunk files | MIT |
| IBM Plex Sans | `ui-ds/fonts/` | SIL OFL 1.1 (`ui-ds/fonts/OFL.txt`) |
| Tabler Icons 3.45.0 | `ui-ds/icons.svg`, `ui-ds/icons/` | MIT (`ui-ds/icons/LICENSE.txt`) |
| Halftone wave | `ui-ds/wave.js` | Payara's own code, from payara/observability-tool |

## Follow-ups

- Take the tokens from the payara-ds repository instead of the copy in `ui-ds/tokens.css`.
- More themes (see above).
- Move to the observability-tool React components when their package is published.
- A clearer message when generation fails on the server.
