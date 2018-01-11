'use strict';

const DomainsView = Class({

    // Constants
    DOMAINS_API_PATH: '/domains?path=/&token={{& token }}',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

    usageApiBaseUrl: undefined,
    token: undefined,

    // Templates
    domainsListTemplate: '#ui-domains-list-template',
    domainsListLoadingTemplate: '#ui-domains-list-loading-template',
    errorMessageTemplate: '#ui-error-message-template',

    // Components
    errorMessageContainer: '#ui-error-message',
    domainsTable: '#ui-domains-table',

    costCalculator: undefined,

    initialize: function (usageApiBaseUrl, token) {
        this.usageApiBaseUrl = usageApiBaseUrl;
        this.token = token;

        _.bindAll(this, ... _.functions(this));

        this.loadPage();
    },

    renderDomainsList: function (domains) {
        const html = $(this.domainsListTemplate).html();
        const rendered = Mustache.render(html, {domains: domains, token: this.token});

        $('tbody', this.domainsTable).html(rendered);
    },

    renderDomainsListLoading: function () {
        const html = $(this.domainsListLoadingTemplate).html();
        const rendered = Mustache.render(html);

        $('tbody', this.domainsTable).html(rendered);
    },

    loadPage: function () {
        this.renderDomainsListLoading();

        const renderedUrl = Mustache.render(this.usageApiBaseUrl + this.DOMAINS_API_PATH, {token: this.token});
        $.get(renderedUrl, this.renderDomainsList)
            .fail(this.parseErrorResponse);
    },

    parseErrorResponse: function (response) {
        this.renderDomainsList();

        if (response.status >= 200 && response.status < 600) {
            try {
                console.log(JSON.parse(response.responseText));
            } catch (e) {
                console.log('Unable to parse error response.');
            }
        } else {
            this.renderErrorMessage(this.DEFAULT_ERROR_MESSAGE);
        }
    },

    renderErrorMessage: function (errorMessage) {
        const html = $(this.errorMessageTemplate).html();
        const rendered = Mustache.render(html, {errorMessage: errorMessage});
        $(this.errorMessageContainer).html(rendered);
    }

});
