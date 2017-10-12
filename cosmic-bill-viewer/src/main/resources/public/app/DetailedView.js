'use strict';

const DetailedView = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    API_DATE_FORMAT: 'YYYY-MM-DD',
    MONTH_SELECTOR_FORMAT: 'YYYY-MM',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    DETAILED_USAGE_PATH: '/detailed?path={{& path }}&from={{& from }}&to={{& to }}&unit=GB&token={{& token }}',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

    usageApiBaseUrl: undefined,
    path: undefined,
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
    domainsDetailedListTemplate: '#ui-domains-detailed-list-template',
    domainsListLoadingTemplate: '#ui-domains-list-loading-template',
    errorMessageTemplate: '#ui-error-message-template',

    // Components
    errorMessageContainer: '#ui-error-message',
    monthSelectorComponent: '#ui-month-selector',
    domainsTable: '#ui-domains-table',
    domainsTableHeaders: 'thead tr th.ui-domains-table-header',
    selectedDomainsTableHeader: 'thead tr th.ui-domains-table-header[data-selected="true"]',

    costCalculator: undefined,

    initialize: function (
        usageApiBaseUrl,
        path,
        token,
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFee,
        innovationFee
    ) {
        this.usageApiBaseUrl = usageApiBaseUrl;
        this.path = path;
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

    renderDomainsList: function (domains) {
        const html = $(this.domainsDetailedListTemplate).html();
        const rendered = Mustache.render(html, domains);
        console.log("rendered");
        console.log(rendered);
        $('tbody', this.domainsTable).html(rendered);
    },

    renderDomainsListLoading: function () {
        const html = $(this.domainsListLoadingTemplate).html();
        const rendered = Mustache.render(html);

        $('tbody', this.domainsTable).html(rendered);
    },

    monthSelectorComponentOnChange: function (event) {
        event.preventDefault();
        this.renderDomainsListLoading();

        this.costCalculator = new CostCalculator(
            this.cpuPrice,
            this.memoryPrice,
            this.storagePrice,
            this.publicIpPrice,
            this.serviceFee,
            this.innovationFee
        );

        const selectedMonth = $(this.monthSelectorComponent).datepicker('getFormattedDate');
        const selectedDomainsTableHeader = $(this.selectedDomainsTableHeader, this.domainsTable);

        const from = moment(selectedMonth, this.MONTH_SELECTOR_FORMAT);
        const now = moment();
        const to = (_.isEqual(from.month(), now.month()) && $(this.untilTodayCheckbox).prop('checked'))
            ? now
            : moment(selectedMonth, this.MONTH_SELECTOR_FORMAT).add(1, 'months');

        const renderedUrl = Mustache.render(this.usageApiBaseUrl + this.DETAILED_USAGE_PATH, {
            path: this.path,
            from: from.format(this.API_DATE_FORMAT),
            to: to.format(this.API_DATE_FORMAT),
            token: this.token
        });

        $.get(renderedUrl, this.parseDomainsResultDetailed).fail(this.parseErrorResponse);
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

    parseDomainsResultDetailed: function (data) {
        this.costCalculator.calculateDomainCosts(data);
        this.renderDomainsList(data);
    },

    parseErrorResponse: function (response) {
        console.log("Detailed - parseErrorResponse");
        console.log(response);
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
        console.log("Detailed - renderErrorMessage");
        console.log(errorMessage);
        const html = $(this.errorMessageTemplate).html();
        const rendered = Mustache.render(html, {errorMessage: errorMessage});
        $(this.errorMessageContainer).html(rendered);
    }

});
