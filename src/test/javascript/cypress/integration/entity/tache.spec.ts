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

describe('Tache e2e test', () => {
  const tachePageUrl = '/tache';
  const tachePageUrlPattern = new RegExp('/tache(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const tacheSample = { nom: 'Chicken b Account', prixUnitaire: 6235, disponible: false, nuid: 'bus Auvergne circuit' };

  let tache: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/taches+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/taches').as('postEntityRequest');
    cy.intercept('DELETE', '/api/taches/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (tache) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/taches/${tache.id}`,
      }).then(() => {
        tache = undefined;
      });
    }
  });

  it('Taches menu should load Taches page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tache');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Tache').should('exist');
    cy.url().should('match', tachePageUrlPattern);
  });

  describe('Tache page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tachePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Tache page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/tache/new$'));
        cy.getEntityCreateUpdateHeading('Tache');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tachePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/taches',
          body: tacheSample,
        }).then(({ body }) => {
          tache = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/taches+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [tache],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(tachePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Tache page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tache');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tachePageUrlPattern);
      });

      it('edit button click should load edit Tache page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Tache');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tachePageUrlPattern);
      });

      it('last delete button click should delete instance of Tache', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tache').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tachePageUrlPattern);

        tache = undefined;
      });
    });
  });

  describe('new Tache page', () => {
    beforeEach(() => {
      cy.visit(`${tachePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Tache');
    });

    it('should create an instance of Tache', () => {
      cy.get(`[data-cy="nom"]`).type('Dram').should('have.value', 'Dram');

      cy.get(`[data-cy="prixUnitaire"]`).type('31271').should('have.value', '31271');

      cy.get(`[data-cy="description"]`).type('transmit up').should('have.value', 'transmit up');

      cy.get(`[data-cy="disponible"]`).should('not.be.checked');
      cy.get(`[data-cy="disponible"]`).click().should('be.checked');

      cy.get(`[data-cy="nuid"]`).type('Augustins Roumanie transmitter').should('have.value', 'Augustins Roumanie transmitter');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        tache = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', tachePageUrlPattern);
    });
  });
});
