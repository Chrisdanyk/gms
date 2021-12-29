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

describe('Garage e2e test', () => {
  const garagePageUrl = '/garage';
  const garagePageUrlPattern = new RegExp('/garage(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const garageSample = { nom: 'optical Bike', nuid: 'reboot Optimized' };

  let garage: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/garages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/garages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/garages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (garage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/garages/${garage.id}`,
      }).then(() => {
        garage = undefined;
      });
    }
  });

  it('Garages menu should load Garages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('garage');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Garage').should('exist');
    cy.url().should('match', garagePageUrlPattern);
  });

  describe('Garage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(garagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Garage page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/garage/new$'));
        cy.getEntityCreateUpdateHeading('Garage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', garagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/garages',
          body: garageSample,
        }).then(({ body }) => {
          garage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/garages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [garage],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(garagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Garage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('garage');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', garagePageUrlPattern);
      });

      it('edit button click should load edit Garage page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Garage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', garagePageUrlPattern);
      });

      it('last delete button click should delete instance of Garage', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('garage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', garagePageUrlPattern);

        garage = undefined;
      });
    });
  });

  describe('new Garage page', () => {
    beforeEach(() => {
      cy.visit(`${garagePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Garage');
    });

    it('should create an instance of Garage', () => {
      cy.get(`[data-cy="nom"]`).type('Grèce motivating Midi-Pyrénées').should('have.value', 'Grèce motivating Midi-Pyrénées');

      cy.get(`[data-cy="addresse"]`).type('monitor Tools array').should('have.value', 'monitor Tools array');

      cy.get(`[data-cy="email"]`).type('Gabrielle_Francois65@yahoo.fr').should('have.value', 'Gabrielle_Francois65@yahoo.fr');

      cy.get(`[data-cy="telephone"]`).type('0147702272').should('have.value', '0147702272');

      cy.get(`[data-cy="rccm"]`).type('synthesize Tools Silver').should('have.value', 'synthesize Tools Silver');

      cy.get(`[data-cy="url"]`).type('http://timoléon.com').should('have.value', 'http://timoléon.com');

      cy.get(`[data-cy="nuid"]`).type('Account Mouse seize').should('have.value', 'Account Mouse seize');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        garage = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', garagePageUrlPattern);
    });
  });
});
