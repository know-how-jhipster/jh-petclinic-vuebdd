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

describe('Specialties e2e test', () => {
  const specialtiesPageUrl = '/specialties';
  const specialtiesPageUrlPattern = new RegExp('/specialties(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const specialtiesSample = { name: 'mobile sensor copy' };

  let specialties;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/specialties+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/specialties').as('postEntityRequest');
    cy.intercept('DELETE', '/api/specialties/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (specialties) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/specialties/${specialties.id}`,
      }).then(() => {
        specialties = undefined;
      });
    }
  });

  it('Specialties menu should load Specialties page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('specialties');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Specialties').should('exist');
    cy.url().should('match', specialtiesPageUrlPattern);
  });

  describe('Specialties page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(specialtiesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Specialties page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/specialties/new$'));
        cy.getEntityCreateUpdateHeading('Specialties');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', specialtiesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/specialties',
          body: specialtiesSample,
        }).then(({ body }) => {
          specialties = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/specialties+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [specialties],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(specialtiesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Specialties page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('specialties');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', specialtiesPageUrlPattern);
      });

      it('edit button click should load edit Specialties page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Specialties');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', specialtiesPageUrlPattern);
      });

      it('edit button click should load edit Specialties page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Specialties');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', specialtiesPageUrlPattern);
      });

      it('last delete button click should delete instance of Specialties', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('specialties').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', specialtiesPageUrlPattern);

        specialties = undefined;
      });
    });
  });

  describe('new Specialties page', () => {
    beforeEach(() => {
      cy.visit(`${specialtiesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Specialties');
    });

    it('should create an instance of Specialties', () => {
      cy.get(`[data-cy="name"]`).type('Franc innovative').should('have.value', 'Franc innovative');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        specialties = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', specialtiesPageUrlPattern);
    });
  });
});
