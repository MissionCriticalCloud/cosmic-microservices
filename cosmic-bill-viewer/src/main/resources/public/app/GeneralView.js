'use strict';

const GeneralView = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    API_DATE_FORMAT: 'YYYY-MM-DD',
    MONTH_SELECTOR_FORMAT: 'YYYY-MM',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    GENERAL_USAGE_PATH: '/general?path=/&from={{& from }}&to={{& to }}&unit=GB&sortBy={{& sortBy }}&sortOrder={{& sortOrder }}&token={{& token }}',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

    usageApiBaseUrl: undefined,
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
    domainsListTemplate: '#ui-domains-list-template',
    domainsListLoadingTemplate: '#ui-domains-list-loading-template',
    errorMessageTemplate: '#ui-error-message-template',

    // Components
    errorMessageContainer: '#ui-error-message',
    monthSelectorComponent: '#ui-month-selector',
    untilTodayCheckbox: '#ui-month-today-checkbox',
    domainsTable: '#ui-domains-table',
    domainsTableHeaders: 'thead tr th.ui-domains-table-header',
    selectedDomainsTableHeader: 'thead tr th.ui-domains-table-header[data-selected="true"]',
    domainsTableRows: 'tbody tr',

    costCalculator: undefined,

    initialize: function (
        usageApiBaseUrl,
        token,
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFee,
        innovationFee
    ) {
        this.usageApiBaseUrl = usageApiBaseUrl;
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
        $(this.domainsTable).on('click', this.domainsTableRows, this.domainsTableRowOnClick);

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
        const html = $(this.domainsListTemplate).html();
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

        const renderedUrl = Mustache.render(this.usageApiBaseUrl + this.GENERAL_USAGE_PATH, {
            from: from.format(this.API_DATE_FORMAT),
            to: to.format(this.API_DATE_FORMAT),
            sortBy: selectedDomainsTableHeader.attr(this.DATA_SORT_BY),
            sortOrder: selectedDomainsTableHeader.attr(this.DATA_SORT_ORDER),
            token: this.token
        });

        $.get(renderedUrl, this.parseDomainsResultGeneral).fail(this.parseErrorResponse);
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

    domainsTableRowOnClick: function (event) {
        event.preventDefault();
        const target = event.currentTarget;
        const domainPath = $(target).data('domainPath');
        if (typeof domainPath !== 'undefined') {
            window.open('/detailed?path=' + domainPath + '&token=' + this.token, '_blank');
        }
    },

    parseDomainsResultGeneral: function (data) {
        this.costCalculator.calculateDomainCosts(data.domains, false);
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
