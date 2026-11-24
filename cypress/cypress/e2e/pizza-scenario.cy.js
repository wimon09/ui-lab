// QP-01 A custom pizza reaches the admin list
// Goal: verify the restrictions, login requirement, and admin list entry.
function stamp() {
  const date = new Date()
  const two = (number) => String(number).padStart(2, '0')
  const day =
    date.getFullYear() +
    two(date.getMonth() + 1) +
    two(date.getDate())
  const time =
    two(date.getHours()) +
    two(date.getMinutes()) +
    two(date.getSeconds())
  return `${day}-${time}`
}

describe('QP-01 A custom pizza reaches the admin list', () => {
  it('follows all seven steps in one browser session', () => {
    // Step 1: open QuickPizza.
    cy.visit('https://quickpizza.grafana.com')
    cy.get('h1').should(
      'have.text',
      'Looking to break out of your pizza routine?',
    )

    // Step 2: reveal the advanced fields.
    cy.contains('label', 'Advanced').click()
    cy.contains('label', 'Max Calories per Slice')
      .siblings('input')
      .should('be.visible')
    cy.get('select').should('be.visible')
    cy.get('#default-checkbox').should('be.visible')
    cy.get('input#pizza-name').should('be.visible')

    // Step 3: enter the restrictions and a unique pizza name.
    const pizzaName = `Lab Pizza ${stamp()}`
    cy.contains('label', 'Max Calories per Slice')
      .siblings('input')
      .clear()
      .type('600')
      .should('have.value', '600')
    cy.get('select').select('Knife')
    cy.contains('option', 'Knife').should('be.selected')
    cy.get('#default-checkbox').check().should('be.checked')
    cy.get('input#pizza-name').type(pizzaName)
    cy.get('input#pizza-name').should('have.value', pizzaName)

    // Step 4: request a pizza and verify the recommendation and API response.
    cy.intercept('POST', '/api/pizza').as('pizza')
    cy.get('button[name="pizza-please"]').click()
    cy.wait('@pizza').its('response.statusCode').should('eq', 200)
    cy.get('@pizza').then(({ request, response }) => {
      expect(request.body.maxCaloriesPerSlice).to.equal(600)
      const { pizza, calories } = response.body
      cy.get('#recommendations').should('contain', `Name: ${pizzaName}`)
      cy.contains('#recommendations p', 'Tool:')
        .should('have.text', `Tool: ${pizza.tool}`)
        .and('not.contain', 'Tool: Knife')
      cy.contains('#recommendations p', 'Calories per slice:')
        .should('have.text', `Calories per slice: ${calories}`)
      expect(Number(calories)).to.be.at.most(600)
      cy.wrap(pizza.tool).as('tool')
    })
    cy.screenshot('qp01-step4-recommendation')

    // Step 5: try to rate without logging in.
    cy.get('button[name="rate-5"]').click()
    cy.get('#rate-result').should('have.text', 'Please log in first.')
    cy.screenshot('qp01-step5-login-needed')

    // Step 6: log in on the admin page in this same test session.
    cy.adminLogin('admin', 'admin')
    cy.contains('Latest pizza recommendations').should('be.visible')

    // Step 7: verify the new pizza and its tool in the admin list.
    cy.get('@tool').then((tool) => {
      cy.contains('li', pizzaName).should('contain', `(tool=${tool},`)
    })
    cy.screenshot('qp01-step7-admin-list')
  })
})
