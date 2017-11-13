'use strict';

const StorageView = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    API_DATE_FORMAT: 'YYYY-MM-DD',
    MONTH_SELECTOR_FORMAT: 'YYYY-MM',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    DETAILED_STORAGE_PATH: '/storage/domains/{{& uuid }}?from={{& from }}&to={{& to }}&timeUnit=DAYS&dataUnit=GB&token={{& token }}',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

    usageApiBaseUrl: undefined,
    uuid: undefined,
    token: undefined,
    cpuPrice: undefined,
    memoryPrice: undefined,
    storagePrice: undefined,
    publicIpPrice: undefined,
    serviceFee: undefined,
    innovationFee: undefined,

    // Sorting
    ASCENDING: 'ASC',
    DESCENDING: 'DESC',
    ASCENDING_ICON: '<i class="fa fa-chevron-up" aria-hidden="true"></i>',
    DESCENDING_ICON: '<i class="fa fa-chevron-down" aria-hidden="true"></i>',

    // Data attributes
    DATA_LABEL: 'data-label',
    DATA_SORT_BY: 'data-sort-by',
    DATA_SORT_ORDER: 'data-sort-order',
    DATA_SELECTED: 'data-selected',

    // Templates
    domainDetailedTemplate: '#ui-domains-detailed-list-template',
    domainLoadingTemplate: '#ui-domains-list-loading-template',
    errorMessageTemplate: '#ui-error-message-template',
    noDomainDataTemplate: '#ui-no-domain-template',

    // Components
    errorMessageContainer: '#ui-error-message',
    monthSelectorComponent: '#ui-month-selector',
    domainsTable: '#ui-domains-table',
    domainsTableHeaders: 'thead tr th.ui-domains-table-header',
    selectedDomainsTableHeader: 'thead tr th.ui-domains-table-header[data-selected="true"]',

    costCalculator: undefined,

    initialize: function (
        usageApiBaseUrl,
        uuid,
        token,
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFee,
        innovationFee
    ) {
        this.usageApiBaseUrl = usageApiBaseUrl;
        this.uuid = uuid;
        this.token = token;
        this.cpuPrice = cpuPrice;
        this.memoryPrice = memoryPrice;
        this.storagePrice = storagePrice;
        this.publicIpPrice = publicIpPrice;
        this.serviceFee = serviceFee;
        this.innovationFee = innovationFee;

        numeral.defaultFormat(this.DECIMAL_FORMAT);
        _.bindAll(this, ... _.functions(this));

        $(this.monthSelectorComponent).datepicker().on('changeDate', this.monthSelectorComponentOnChange);
        $(this.domainsTableHeaders, this.domainsTable).on('click', this.domainsTableHeaderOnClick);

        $(this.monthSelectorComponent).datepicker('setUTCDate', $(this.monthSelectorComponent).datepicker('getEndDate'));
    },

    renderDomain: function (domain) {
        const html = domain? $(this.domainDetailedTemplate).html(): $(this.noDomainDataTemplate).html();
        const rendered = Mustache.render(html, domain);
        $('tbody', this.domainsTable).html(rendered);
    },

    renderDomainLoading: function () {
        const html = $(this.domainLoadingTemplate).html();
        const rendered = Mustache.render(html);

        $('tbody', this.domainsTable).html(rendered);
    },

    monthSelectorComponentOnChange: function (event) {
        event.preventDefault();
        this.renderDomainLoading();

        this.costCalculator = new CostCalculator(
            this.cpuPrice,
            this.memoryPrice,
            this.storagePrice,
            this.publicIpPrice,
            this.serviceFee,
            this.innovationFee
        );

        const selectedMonth = $(this.monthSelectorComponent).datepicker('getFormattedDate');

        const from = moment(selectedMonth, this.MONTH_SELECTOR_FORMAT);
        const now = moment();
        const to = (_.isEqual(from.month(), now.month()) && $(this.untilTodayCheckbox).prop('checked'))
            ? now
            : moment(selectedMonth, this.MONTH_SELECTOR_FORMAT).add(1, 'months');
        const renderedUrl = Mustache.render(this.usageApiBaseUrl + this.DETAILED_STORAGE_PATH, {
            uuid: this.uuid,
            from: from.format(this.API_DATE_FORMAT),
            to: to.format(this.API_DATE_FORMAT),
            token: this.token
        });

        $.get(renderedUrl, this.parseDomainResultDetailed).fail(this.parseErrorResponse);
    },

    domainsTableHeaderOnClick: function (event) {
        event.preventDefault();

        const header = $(event.currentTarget);
        const sortOrder = _.isEqual(header.attr(this.DATA_SELECTED), 'true') &&
        _.isEqual(header.attr(this.DATA_SORT_ORDER), this.ASCENDING)
            ? this.DESCENDING
            : this.ASCENDING;
        header.attr(this.DATA_SORT_ORDER, sortOrder);

        $(this.domainsTableHeaders, this.domainsTable).attr(this.DATA_SELECTED, false);
        header.attr(this.DATA_SELECTED, true);
        this.monthSelectorComponentOnChange(event);
    },

    parseDomainResultDetailed: function (domain) {
        this.costCalculator.calculateStorageCosts(domain.usage.storage);
        this.costCalculator.addTotalStorageCosts(domain.usage.storage);
        this.renderDomain(domain);
    },

    parseErrorResponse: function (response) {
        this.renderDomain();

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
