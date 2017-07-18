'use strict';

const DetailedView = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    API_DATE_FORMAT: 'YYYY-MM-DD',
    MONTH_SELECTOR_FORMAT: 'YYYY-MM',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    DETAILED_USAGE_PATH: '/detailed?path={{& path }}&from={{& from }}&to={{& to }}&unit=GB&sortBy={{& sortBy }}&sortOrder={{& sortOrder }}',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

    usageApiBaseUrl: undefined,
    path: undefined,
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
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFee,
        innovationFee
    ) {
        this.usageApiBaseUrl = usageApiBaseUrl;
        this.path = path;
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
        this.renderDomainTableHeaders();
    },

    renderDomainTableHeaders: function () {
        const that = this;
        $(this.domainsTableHeaders, this.domainsTable).each(function () {
            const header = $(this);
            var label = header.attr(that.DATA_LABEL);
            if (_.isEqual(header.attr(that.DATA_SELECTED), 'true')) {
                const sortIcon = _.isEqual(header.attr(that.DATA_SORT_ORDER), that.DESCENDING)
                    ? that.DESCENDING_ICON
                    : that.ASCENDING_ICON;
                label += ' ' + sortIcon;
            }
            header.html(label);
        });
    },

    renderDomainsList: function (domains) {
        const html = $(this.domainsDetailedListTemplate).html();
        const rendered = Mustache.render(html, {domains: domains});

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
            sortBy: selectedDomainsTableHeader.attr(this.DATA_SORT_BY),
            sortOrder: selectedDomainsTableHeader.attr(this.DATA_SORT_ORDER)
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

        this.renderDomainTableHeaders();
        this.monthSelectorComponentOnChange(event);
    },

    parseDomainsResultDetailed: function (data) {
        this.costCalculator.calculateDomainCosts(data.domains, true);
        this.renderDomainsList(data.domains);
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
