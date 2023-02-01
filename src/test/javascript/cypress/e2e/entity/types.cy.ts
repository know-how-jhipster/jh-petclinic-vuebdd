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

describe('Types e2e test', () => {
  const typesPageUrl = '/types';
  const typesPageUrlPattern = new RegExp('/types(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const typesSample = { name: 'transparent Central standardization' };

  let types;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/types+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/types').as('postEntityRequest');
    cy.intercept('DELETE', '/api/types/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (types) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/types/${types.id}`,
      }).then(() => {
        types = undefined;
      });
    }
  });

  it('Types menu should load Types page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('types');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Types').should('exist');
    cy.url().should('match', typesPageUrlPattern);
  });

  describe('Types page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(typesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Types page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/types/new$'));
        cy.getEntityCreateUpdateHeading('Types');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/types',
          body: typesSample,
        }).then(({ body }) => {
          types = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/types+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/types?page=0&size=20>; rel="last",<http://localhost/api/types?page=0&size=20>; rel="first"',
              },
              body: [types],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(typesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Types page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('types');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typesPageUrlPattern);
      });

      it('edit button click should load edit Types page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Types');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typesPageUrlPattern);
      });

      it('edit button click should load edit Types page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Types');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typesPageUrlPattern);
      });

      it('last delete button click should delete instance of Types', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('types').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', typesPageUrlPattern);

        types = undefined;
      });
    });
  });

  describe('new Types page', () => {
    beforeEach(() => {
      cy.visit(`${typesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Types');
    });

    it('should create an instance of Types', () => {
      cy.get(`[data-cy="name"]`).type('Computers Producer SQL').should('have.value', 'Computers Producer SQL');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        types = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', typesPageUrlPattern);
    });
  });
});
