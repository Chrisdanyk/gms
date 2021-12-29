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

describe('Maintenance e2e test', () => {
  const maintenancePageUrl = '/maintenance';
  const maintenancePageUrlPattern = new RegExp('/maintenance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const maintenanceSample = { dateDebut: '2021-12-29', dateFin: '2021-12-29', prixTotal: 65259, nuid: 'Universal' };

  let maintenance: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/maintenances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/maintenances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/maintenances/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (maintenance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/maintenances/${maintenance.id}`,
      }).then(() => {
        maintenance = undefined;
      });
    }
  });

  it('Maintenances menu should load Maintenances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('maintenance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Maintenance').should('exist');
    cy.url().should('match', maintenancePageUrlPattern);
  });

  describe('Maintenance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(maintenancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Maintenance page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/maintenance/new$'));
        cy.getEntityCreateUpdateHeading('Maintenance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', maintenancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/maintenances',
          body: maintenanceSample,
        }).then(({ body }) => {
          maintenance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/maintenances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [maintenance],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(maintenancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Maintenance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('maintenance');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', maintenancePageUrlPattern);
      });

      it('edit button click should load edit Maintenance page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Maintenance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', maintenancePageUrlPattern);
      });

      it('last delete button click should delete instance of Maintenance', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('maintenance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', maintenancePageUrlPattern);

        maintenance = undefined;
      });
    });
  });

  describe('new Maintenance page', () => {
    beforeEach(() => {
      cy.visit(`${maintenancePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Maintenance');
    });

    it('should create an instance of Maintenance', () => {
      cy.get(`[data-cy="dateDebut"]`).type('2021-12-29').should('have.value', '2021-12-29');

      cy.get(`[data-cy="dateFin"]`).type('2021-12-29').should('have.value', '2021-12-29');

      cy.get(`[data-cy="rapportGlobal"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="prixTotal"]`).type('47663').should('have.value', '47663');

      cy.get(`[data-cy="discountTotal"]`).type('46115').should('have.value', '46115');

      cy.get(`[data-cy="nuid"]`).type('1080p').should('have.value', '1080p');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        maintenance = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', maintenancePageUrlPattern);
    });
  });
});
