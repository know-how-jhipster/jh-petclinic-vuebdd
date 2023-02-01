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

describe('Owners e2e test', () => {
  const ownersPageUrl = '/owners';
  const ownersPageUrlPattern = new RegExp('/owners(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ownersSample = {
    firstname: 'Refined Bedfordshire firewall',
    lastname: 'Rupee',
    address: 'invoice Kids',
    telephone: '(709) 492-9879 x5363',
  };

  let owners;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/owners+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/owners').as('postEntityRequest');
    cy.intercept('DELETE', '/api/owners/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (owners) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/owners/${owners.id}`,
      }).then(() => {
        owners = undefined;
      });
    }
  });

  it('Owners menu should load Owners page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('owners');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Owners').should('exist');
    cy.url().should('match', ownersPageUrlPattern);
  });

  describe('Owners page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ownersPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Owners page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/owners/new$'));
        cy.getEntityCreateUpdateHeading('Owners');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ownersPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/owners',
          body: ownersSample,
        }).then(({ body }) => {
          owners = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/owners+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/owners?page=0&size=20>; rel="last",<http://localhost/api/owners?page=0&size=20>; rel="first"',
              },
              body: [owners],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ownersPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Owners page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('owners');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ownersPageUrlPattern);
      });

      it('edit button click should load edit Owners page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Owners');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ownersPageUrlPattern);
      });

      it('edit button click should load edit Owners page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Owners');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ownersPageUrlPattern);
      });

      it('last delete button click should delete instance of Owners', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('owners').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ownersPageUrlPattern);

        owners = undefined;
      });
    });
  });

  describe('new Owners page', () => {
    beforeEach(() => {
      cy.visit(`${ownersPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Owners');
    });

    it('should create an instance of Owners', () => {
      cy.get(`[data-cy="firstname"]`).type('multi-byte').should('have.value', 'multi-byte');

      cy.get(`[data-cy="lastname"]`).type('Upgradable Kansas').should('have.value', 'Upgradable Kansas');

      cy.get(`[data-cy="address"]`).type('enable Mall forecast').should('have.value', 'enable Mall forecast');

      cy.get(`[data-cy="city"]`).type('East Isac').should('have.value', 'East Isac');

      cy.get(`[data-cy="telephone"]`).type('561.737.9275 x2592').should('have.value', '561.737.9275 x2592');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        owners = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ownersPageUrlPattern);
    });
  });
});
