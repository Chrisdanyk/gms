import { entityItemSelector } from '../../support/commands';
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

describe('Engin e2e test', () => {
  const enginPageUrl = '/engin';
  const enginPageUrlPattern = new RegExp('/engin(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const enginSample = { plaque: 'Configurable', type: 'VELO' };

  let engin: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/engins+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/engins').as('postEntityRequest');
    cy.intercept('DELETE', '/api/engins/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (engin) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/engins/${engin.id}`,
      }).then(() => {
        engin = undefined;
      });
    }
  });

  it('Engins menu should load Engins page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('engin');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Engin').should('exist');
    cy.url().should('match', enginPageUrlPattern);
  });

  describe('Engin page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(enginPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Engin page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/engin/new$'));
        cy.getEntityCreateUpdateHeading('Engin');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', enginPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/engins',
          body: enginSample,
        }).then(({ body }) => {
          engin = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/engins+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [engin],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(enginPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Engin page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('engin');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', enginPageUrlPattern);
      });

      it('edit button click should load edit Engin page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Engin');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', enginPageUrlPattern);
      });

      it('last delete button click should delete instance of Engin', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('engin').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', enginPageUrlPattern);

        engin = undefined;
      });
    });
  });

  describe('new Engin page', () => {
    beforeEach(() => {
      cy.visit(`${enginPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Engin');
    });

    it('should create an instance of Engin', () => {
      cy.get(`[data-cy="modele"]`).type('Consultant').should('have.value', 'Consultant');

      cy.get(`[data-cy="plaque"]`).type('Outdoors').should('have.value', 'Outdoors');

      cy.get(`[data-cy="dateFabrication"]`).type('2021-12-29').should('have.value', '2021-12-29');

      cy.get(`[data-cy="type"]`).select('VELO');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        engin = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', enginPageUrlPattern);
    });
  });
});
