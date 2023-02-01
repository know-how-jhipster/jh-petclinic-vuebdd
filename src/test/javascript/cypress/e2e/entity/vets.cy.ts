import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Vets e2e test', () => {
  const vetsPageUrl = '/vets';
  const vetsPageUrlPattern = new RegExp('/vets(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const vetsSample = { firstname: 'Cambridgeshire Shoes', lastname: 'indexing virtual' };

  let vets;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/vets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/vets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/vets/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (vets) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/vets/${vets.id}`,
      }).then(() => {
        vets = undefined;
      });
    }
  });

  it('Vets menu should load Vets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('vets');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Vets').should('exist');
    cy.url().should('match', vetsPageUrlPattern);
  });

  describe('Vets page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(vetsPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Vets page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/vets/new$'));
        cy.getEntityCreateUpdateHeading('Vets');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vetsPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/vets',
          body: vetsSample,
        }).then(({ body }) => {
          vets = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/vets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/vets?page=0&size=20>; rel="last",<http://localhost/api/vets?page=0&size=20>; rel="first"',
              },
              body: [vets],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(vetsPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Vets page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('vets');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vetsPageUrlPattern);
      });

      it('edit button click should load edit Vets page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vets');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vetsPageUrlPattern);
      });

      it('edit button click should load edit Vets page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vets');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vetsPageUrlPattern);
      });

      it('last delete button click should delete instance of Vets', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('vets').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', vetsPageUrlPattern);

        vets = undefined;
      });
    });
  });

  describe('new Vets page', () => {
    beforeEach(() => {
      cy.visit(`${vetsPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Vets');
    });

    it('should create an instance of Vets', () => {
      cy.get(`[data-cy="firstname"]`).type('contingency iterate').should('have.value', 'contingency iterate');

      cy.get(`[data-cy="lastname"]`).type('Ferry COM Ergonomic').should('have.value', 'Ferry COM Ergonomic');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        vets = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', vetsPageUrlPattern);
    });
  });
});
