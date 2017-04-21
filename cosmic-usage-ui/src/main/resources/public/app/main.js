'use strict';

const app = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    API_DATE_FORMAT: 'YYYY-MM-DD',
    MONTH_SELECTOR_FORMAT: 'YYYY-MM',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    USAGE_API_BASE_URL: undefined,
    GENERAL_USAGE_PATH: '/general',
    DETAILED_USAGE_PATH: '/detailed',
    DEFAULT_USAGE_PATH: '?from={{& from }}&to={{& to }}&path={{& path }}&sortBy={{& sortBy }}&sortOrder={{& sortOrder }}&unit=MB',
    DEFAULT_ERROR_MESSAGE: 'Unable to communicate with the Usage API. Please contact your system administrator.',

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
    domainsDetailedListTemplate: '#ui-domains-detailed-list-template',
    printingHeadersTemplate: '#ui-printing-headers-template',
    errorMessageTemplate: '#ui-error-message-template',

    // Components
    errorMessageContainer: '#ui-error-message',
    monthSelectorComponent: '#ui-month-selector',
    untilTodayCheckbox: '#ui-month-today-checkbox',
    detailedViewCheckbox: '#ui-detailed-view-checkbox',
    domainPathField: '#ui-domain-path',
    cpuPriceField: '#ui-cpu-price',
    memoryPriceField: '#ui-memory-price',
    storagePriceField: '#ui-storage-price',
    publicIpPriceField: '#ui-public-ip-price',
    serviceFeePercentageField: '#ui-service-fee-percentage',
    innovationFeePercentageField: '#ui-innovation-fee-percentage',
    generateReportButton: '#ui-generate-report-btn',
    printingHeadersContainer: '#ui-printing-headers',
    domainsTable: '#ui-domains-table',
    domainsTableHeaders: 'thead tr th.ui-domains-table-header',
    selectedDomainsTableHeader: 'thead tr th.ui-domains-table-header[data-selected="true"]',

    costCalcLocal: undefined,

    initialize: function(baseUrl) {
        this.USAGE_API_BASE_URL = baseUrl;

        numeral.defaultFormat(this.DECIMAL_FORMAT);
        _.bindAll(this, ... _.functions(this));

        this.costCalcLocal = new costCalculator(
            this.cpuPriceField,
            this.memoryPriceField,
            this.storagePriceField,
            this.publicIpPriceField,
            this.serviceFeePercentageField,
            this.innovationFeePercentageField
        );

        $(this.monthSelectorComponent).datepicker('setDate', new Date());
        this.renderPrintingHeaders();
        this.renderDomainTableHeaders();
        this.renderDomainsList();
        this.renderCheckboxes();

        $(this.generateReportButton).on('click', this.generateReportButtonOnClick);
        $(this.domainsTableHeaders, this.domainsTable).on('click', this.domainsTableHeaderOnClick);
    },

    renderCheckboxes: function() {
        $(this.untilTodayCheckbox).on('click', this.checkBoxesOnClick);
        $(this.detailedViewCheckbox).on('click', this.checkBoxesOnClick);
    },

    renderPrintingHeaders: function() {
        const selectedMonth = $(this.monthSelectorComponent).datepicker('getFormattedDate');

        const selectedMonthFormatted = moment(selectedMonth, this.MONTH_SELECTOR_FORMAT)
            .format(this.SELECTED_MONTH_HUMAN_FORMAT);

        const cpuPriceFormatted = numeral($(this.cpuPriceField).val()).format();
        const memoryPriceFormatted = numeral($(this.memoryPriceField).val()).format();
        const storagePriceFormatted = numeral($(this.storagePriceField).val()).format();
        const publicIpPriceFormatted = numeral($(this.publicIpPriceField).val()).format();

        const html = $(this.printingHeadersTemplate).html();
        const rendered = Mustache.render(html, {
            selectedMonth: selectedMonthFormatted,
            cpuPrice: cpuPriceFormatted,
            memoryPrice: memoryPriceFormatted,
            storagePrice: storagePriceFormatted,
            publicIpPrice: publicIpPriceFormatted
        });
        $(this.printingHeadersContainer).html(rendered);
    },

    renderDomainTableHeaders: function() {
        const that = this;
        $(this.domainsTableHeaders, this.domainsTable).each(function() {
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

    renderDomainsList: function(domains) {
        var html = '';
        if ($(this.detailedViewCheckbox).prop('checked')) {
            html = $(this.domainsDetailedListTemplate).html();
        } else {
            html = $(this.domainsListTemplate).html();
        }

        const rendered = Mustache.render(html, { domains: domains });

        $('tbody', this.domainsTable).html(rendered);
    },

    generateReportButtonOnClick: function(event) {
        event.preventDefault();

        const selectedMonth = $(this.monthSelectorComponent).datepicker('getFormattedDate');
        const selectedDomainsTableHeader = $(this.selectedDomainsTableHeader, this.domainsTable);

        const from = moment(selectedMonth, this.MONTH_SELECTOR_FORMAT);
        const now = moment();
        const to = (_.isEqual(from.month(), now.month()) && $(this.untilTodayCheckbox).prop('checked'))
            ? now
            : moment(selectedMonth, this.MONTH_SELECTOR_FORMAT).add(1, 'months');

        const path = $(this.domainPathField).val();

        var url = '';
        var parseFunction = undefined;

        if ($(this.detailedViewCheckbox).prop('checked')) {
            url = this.DETAILED_USAGE_PATH + this.DEFAULT_USAGE_PATH;
            parseFunction = this.parseDomainsResultDetailed;
        } else {
            url = this.GENERAL_USAGE_PATH + this.DEFAULT_USAGE_PATH;
            parseFunction = this.parseDomainsResultGeneral;
        }

        const renderedUrl = Mustache.render(this.USAGE_API_BASE_URL + url, {
            from: from.format(this.API_DATE_FORMAT),
            to: to.format(this.API_DATE_FORMAT),
            path: path,
            sortBy: selectedDomainsTableHeader.attr(this.DATA_SORT_BY),
            sortOrder: selectedDomainsTableHeader.attr(this.DATA_SORT_ORDER)
        });

        $.get(renderedUrl, parseFunction).fail(this.parseErrorResponse);
    },

    domainsTableHeaderOnClick: function(event) {
        event.preventDefault();

        const header = $(event.currentTarget);
        const sortOrder = _.isEqual(header.attr(this.DATA_SELECTED), 'true') && _.isEqual(header.attr(this.DATA_SORT_ORDER), this.ASCENDING)
            ? this.DESCENDING
            : this.ASCENDING;
        header.attr(this.DATA_SORT_ORDER, sortOrder);

        $(this.domainsTableHeaders, this.domainsTable).attr(this.DATA_SELECTED, false);
        header.attr(this.DATA_SELECTED, true);

        this.renderDomainTableHeaders();
        $(this.generateReportButton).click();
    },

    checkBoxesOnClick: function() {
        $(this.generateReportButton).click();
    },

    parseDomainsResultGeneral: function(data) {
        this.renderPrintingHeaders();
        this.costCalcLocal.calculateDomainsCosts(data.domains, false);
        this.renderDomainsList(data.domains);
    },

    parseDomainsResultDetailed: function(data) {
        this.renderPrintingHeaders();
        this.costCalcLocal.calculateDomainsCosts(data.domains, true);
        this.renderDomainsList(data.domains);
    },

    parseErrorResponse: function(response) {
        if (response.status >= 200 && response.status < 600) {
            try {
                console.log(JSON.parse(response.responseText));
            } catch (e) {
                console.log('Unable to parse error response.');
            }
        }
        this.renderErrorMessage(this.DEFAULT_ERROR_MESSAGE);
    },

    renderErrorMessage: function(errorMessage) {
        const html = $(this.errorMessageTemplate).html();
        const rendered = Mustache.render(html, { errorMessage: errorMessage });
        $(this.errorMessageContainer).html(rendered);
    }

});
