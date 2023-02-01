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

describe('Pets e2e test', () => {
  const petsPageUrl = '/pets';
  const petsPageUrlPattern = new RegExp('/pets(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const petsSample = { name: 'Egyptian', birthdate: '2023-01-31' };

  let pets;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/pets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/pets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/pets/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (pets) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pets/${pets.id}`,
      }).then(() => {
        pets = undefined;
      });
    }
  });

  it('Pets menu should load Pets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('pets');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Pets').should('exist');
    cy.url().should('match', petsPageUrlPattern);
  });

  describe('Pets page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(petsPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Pets page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/pets/new$'));
        cy.getEntityCreateUpdateHeading('Pets');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', petsPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/pets',
          body: petsSample,
        }).then(({ body }) => {
          pets = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/pets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/pets?page=0&size=20>; rel="last",<http://localhost/api/pets?page=0&size=20>; rel="first"',
              },
              body: [pets],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(petsPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Pets page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pets');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', petsPageUrlPattern);
      });

      it('edit button click should load edit Pets page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Pets');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', petsPageUrlPattern);
      });

      it('edit button click should load edit Pets page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Pets');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', petsPageUrlPattern);
      });

      it('last delete button click should delete instance of Pets', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('pets').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', petsPageUrlPattern);

        pets = undefined;
      });
    });
  });

  describe('new Pets page', () => {
    beforeEach(() => {
      cy.visit(`${petsPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Pets');
    });

    it('should create an instance of Pets', () => {
      cy.get(`[data-cy="name"]`).type('Account schemas facilitate').should('have.value', 'Account schemas facilitate');

      cy.get(`[data-cy="birthdate"]`).type('2023-02-01').blur().should('have.value', '2023-02-01');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        pets = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', petsPageUrlPattern);
    });
  });
});
