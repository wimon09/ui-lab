# UI Testing Lab Results

## Part 0

- Step 0.1, Chrome version: Google Chrome 158.0.8127.37 (Apple silicon).
- Step 0.3, ChromeDriver version: ChromeDriver 158.0.8127.37.
- Step 0.4, locator of Pizza, Please!: `button[name="pizza-please"]`.
- Step 0.4, locator of Excluded tools: `select`.
- Step 0.4, locator of Must be vegetarian: `#default-checkbox`.
- Step 0.4, locator of Custom Pizza Name: `input#pizza-name`.
- Step 0.5, pizza name, tool and calories of the run by hand: `Lab Pizza 20261124-223947`, Scissors, 350 calories per slice.

| Step | Actual result | Pass or fail |
|---|---|---|
| 1 | QuickPizza opened and the heading read “Looking to break out of your pizza routine?” | Pass |
| 2 | Advanced revealed Max Calories per Slice, Excluded tools, Must be vegetarian, and Custom Pizza Name. | Pass |
| 3 | Set 600 calories, excluded Knife, checked vegetarian, and entered `Lab Pizza 20261124-223947`. | Pass |
| 4 | Recommendation showed that pizza with Scissors and 350 calories per slice. | Pass |
| 5 | Rating while logged out displayed “Please log in first.” | Pass |
| 6 | Signed in to `/admin` as the demo account `admin`; the Latest pizza recommendations page appeared. | Pass |
| 7 | The admin list showed `Lab Pizza 20261124-223947`. | Pass |

## Part 1: Selenium IDE

- Firefox version and Node.js version: Firefox 160.0.1; Node.js v22.14.0.
- Step 1.4, failed check and test output: intentionally expecting `Received` instead of `Received!` failed with `Actual value 'Received!' did not match 'Received'`. After restoring the correct assertion, the final project run reported `Tests: 2 passed, 2 total`.
- Step 1.5, target chosen for Submit: `xpath=//button[@type='submit']`. The recorder's original `css=.btn` is a presentation class shared by buttons and can match the wrong button if one is inserted earlier; the replacement identifies the submit button by its semantic type.
- Step 1.6, commands changed, added or deleted after recording: the saved project uses explicit `waitForElementVisible` commands and title/text/value/state assertions; it includes the Web Form and QP-01 tests. Note: this `.side` project was edited and validated with `selenium-side-runner`; the Firefox recording session was not completed/exported as a new project.
- Step 1.6, pizza names from two successful runs: `Lab Pizza 20261124-224207` and `Lab Pizza 20261124-224244`.
- Step 1.6, `Tests:` result for both successful runs: `2 passed, 2 total`.
- Step 1.7, Java line to rewrite: a raw generated line such as `calories.sendKeys("600");` must clear the prefilled 1000 first, or it would enter 1000600. The companion file in this repository already uses `calories.clear();` before typing.

## Part 4: Cypress

- `node --version`, `npm --version`, `npx cypress version`: v22.14.0, 10.9.2, Cypress 16.2.1.
- Step 4.3, failure message for the intentionally wrong title: `Timed out retrying after 10000ms`.
- Step 4.6, POST `/api/pizza` results from the two recorded successful runs:

| Run | Pizza name | Tool | Calories per slice |
|---|---|---|---:|
| 1 | `Lab Pizza 20261124-224607` | Pizza cutter | 575 |
| 2 | `Lab Pizza 20261124-224651` | Pizza cutter | 325 |

- Step 4.7, admin list: Cypress verified that each run's pizza appeared in a list item with the same tool returned by the recommendation. For example, run 2 was checked as `Lab Pizza 20261124-224651` with `(tool=Pizza cutter, ...)`.
- Step 4.8, full-run results:

| Run | Result | Duration | Pizza name |
|---|---|---:|---|
| 1 | Both specs passed (2 tests total); QP-01 passed. | 13 seconds combined (web form 2 s; QP-01 11 s). | `Lab Pizza 20261124-224607` |
| 2 | Both specs passed (2 tests total); QP-01 passed. | 18 seconds combined (web form 3 s; QP-01 15 s). | `Lab Pizza 20261124-224651` |

- Additional final verification: `cd cypress && npm test` passed both specs (web form 1 test; QP-01 1 test); QP-01 produced three screenshots and a video. Cypress runs each spec in a separate process because the local Cypress/Chrome combination failed when switching origins between specs in one process.
- Step 4.9 (optional), browser and result: not run in a second browser.

## Part B: Reflection

### 1. Calories locator and id

The QuickPizza page reuses the id `floating_filled` on three number inputs, so selecting that id does not uniquely identify Max Calories per Slice. The tests instead find the input relative to its visible label, which identifies the intended field. I would give that input a unique id such as `max-calories-per-slice` and connect its label with a matching `for` attribute. A unique, meaningful id would make the locator simpler and less dependent on the page's DOM layout.

### 2. Test oracle in QP-01 Step 7

This is a consistency oracle: it checks that the tool shown in the admin list is the same tool returned with the recommendation. It would catch a fault where the recommendation uses one tool but the server stores or displays a different tool for that pizza. In the Cypress run, the selected tool was Pizza cutter, and the admin-list assertion checked that same value.

### 3. Checking that a pizza is vegetarian

The test could assert that the returned pizza is explicitly marked vegetarian, for example `pizza.isVegetarian === true`, or validate that every ingredient belongs to an approved vegetarian set. The current checked fields and recommendation assertions do not prove that the recipe itself is vegetarian. The page or API would need to expose a vegetarian flag or enough recipe/ingredient data for the test to evaluate that property.

### 4. Comparing the two tools

The Selenium IDE project uses explicit `waitForElementVisible` commands with a 10-second limit before interacting with dynamic QuickPizza fields and results. Cypress retries queries and assertions until they pass or time out, and the QP-01 test also waits for the `/api/pizza` response; the successful runs took about 11–15 seconds for QP-01. Recording and editing the Selenium IDE flow is a quick way to start, while Cypress took more direct JavaScript work but made API assertions, custom commands, and the end-to-end flow straightforward to express. Cypress's spec output, screenshots, and videos made the result easy to review; the Selenium runner gave a compact command log and pass/fail report.

### 5. Tool choice for Project 2

I would choose Cypress. Its retrying assertions and explicit API interception made it practical to check both the visible recommendation and the response constraints, rather than only replaying clicks. It also produced screenshots and videos for the full QP-01 run, which provide useful evidence when investigating a failure.

### 6. Part 1: Selenium IDE Submit target

The recorder chose `css=.btn`, a styling class that can be shared by multiple buttons; adding a button earlier on the page could make the test click the wrong one. The project uses `xpath=//button[@type='submit']` instead, which selects the element by its submit-button role rather than its appearance. This is more stable if the page's CSS classes or layout change.

### 7. Part 4: Cypress test boundary

QuickPizza uses a cookie to keep a browser on the same backend server, and the admin list only shows pizzas stored on that server. Cypress clears cookies before each test. If steps 6 and 7 ran in a second test, the cleared cookie could send the admin visit to a different server, where the pizza created in steps 1–5 would not appear; keeping all seven steps in one test preserves the server affinity.

## Scope and evidence notes

This repository contains the selected Selenium IDE and Cypress parts. Part 2 (Selenium WebDriver) and Part 3 (Robot Framework) were not selected. The Part 1 `.side` file passes in the runner, but was edited rather than newly exported from the unfinished Firefox recording session; the Java companion is hand-authored rather than an IDE export. The Part 0 manual run and the two Cypress run values above are retained from the recorded work history; the exact raw console transcript for each historical run is not included.
