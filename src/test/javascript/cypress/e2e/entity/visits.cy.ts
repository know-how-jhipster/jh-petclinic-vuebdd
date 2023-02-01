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

describe('Visits e2e test', () => {
  const visitsPageUrl = '/visits';
  const visitsPageUrlPattern = new RegExp('/visits(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const visitsSample = { visitdate: '2023-02-01T10:07:33.616Z', description: 'Assurance' };

  let visits;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/visits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/visits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/visits/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (visits) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/visits/${visits.id}`,
      }).then(() => {
        visits = undefined;
      });
    }
  });

  it('Visits menu should load Visits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('visits');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Visits').should('exist');
    cy.url().should('match', visitsPageUrlPattern);
  });

  describe('Visits page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(visitsPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Visits page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/visits/new$'));
        cy.getEntityCreateUpdateHeading('Visits');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', visitsPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/visits',
          body: visitsSample,
        }).then(({ body }) => {
          visits = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/visits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/visits?page=0&size=20>; rel="last",<http://localhost/api/visits?page=0&size=20>; rel="first"',
              },
              body: [visits],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(visitsPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Visits page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('visits');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', visitsPageUrlPattern);
      });

      it('edit button click should load edit Visits page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Visits');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', visitsPageUrlPattern);
      });

      it('edit button click should load edit Visits page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Visits');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', visitsPageUrlPattern);
      });

      it('last delete button click should delete instance of Visits', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('visits').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', visitsPageUrlPattern);

        visits = undefined;
      });
    });
  });

  describe('new Visits page', () => {
    beforeEach(() => {
      cy.visit(`${visitsPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Visits');
    });

    it('should create an instance of Visits', () => {
      cy.get(`[data-cy="visitdate"]`).type('2023-01-31T21:57').blur().should('have.value', '2023-01-31T21:57');

      cy.get(`[data-cy="description"]`).type('port Kwacha').should('have.value', 'port Kwacha');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        visits = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', visitsPageUrlPattern);
    });
  });
});
