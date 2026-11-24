# UI lab

This repository contains Part 1 (Selenium IDE) and Part 4 (Cypress) of the UI testing lab.
See `RESULTS.md` for verification results and reflection notes.

## Selenium IDE

`selenium-ide/ui-lab.side` contains the Selenium IDE web-form and QuickPizza customer tests. From that folder, run `npx --yes selenium-side-runner@4.0.13 ui-lab.side`; make sure ChromeDriver is on `PATH`.

## Cypress

`cypress/` contains the Cypress web-form test and the full QP-01 scenario, including screenshots and video evidence. From that folder, run `npm install` once and then `npm test`. The script starts one Cypress process per spec because this environment's Cypress/Chrome combination fails when the two specs switch origins in a single process.
