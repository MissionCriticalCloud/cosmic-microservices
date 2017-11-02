'use strict';

const GeneralView = Class({

    // Constants
    DECIMAL_FORMAT: '0,0.00',
    SELECTED_MONTH_HUMAN_FORMAT: 'MMMM YYYY',
    GENERAL_USAGE_PATH: '/general?path=/&token={{& token }}',
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
    domainsTableTotalReport: 'tbody tr td.complete-report',
    domainsTableComputeReport: 'tbody tr td.compute-report',
    domainsTableStorageReport: 'tbody tr td.storage-report',
    domainsTableNetworkingReport: 'tbody tr td.networking-report',

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

        this.loadPage();
        $(this.domainsTableHeaders, this.domainsTable).on('click', this.domainsTableHeaderOnClick);
        $(this.domainsTable).on('click', this.domainsTableTotalReport, this.domainsTableRowOnClick);
        $(this.domainsTable).on('click', this.domainsTableComputeReport, this.domainsTableComputeOnClick);
        $(this.domainsTable).on('click', this.domainsTableStorageReport, this.domainsTableStorageOnClick);
        $(this.domainsTable).on('click', this.domainsTableNetworkingReport, this.domainsTableNetworkingOnClick);

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

    loadPage: function () {
        this.renderDomainsListLoading();

        const renderedUrl = Mustache.render(this.usageApiBaseUrl + this.GENERAL_USAGE_PATH, {
            token: this.token
        });

        $.get(renderedUrl, this.parseDomainsResultGeneral).fail(this.parseErrorResponse);
    },


    domainsTableRowOnClick: function (event) {
        event.preventDefault();
        const target = event.currentTarget;
        const domainPath = $(target).data('domainPath');
        if (typeof domainPath !== 'undefined') {
            window.open('/detailed?path=' + domainPath + '&api=detailed' + '&uuid=' + '&token=' + this.token, '_blank');
        }
    },

    domainsTableComputeOnClick: function (event) {
        event.preventDefault();
        const target = event.currentTarget;
        const domainUuid = $(target).data('domainUuid');
        if (typeof domainUuid !== 'undefined') {
            window.open('/compute/' + domainUuid + '?'+ 'token=' + this.token, '_blank');
        }
    },

    domainsTableStorageOnClick: function (event) {
        event.preventDefault();
        const target = event.currentTarget;
        const domainUuid = $(target).data('domainUuid');
        if (typeof domainUuid !== 'undefined') {
            window.open('/storage/' + domainUuid + '?'+ 'token=' + this.token, '_blank');
        }
    },

    domainsTableNetworkingOnClick: function (event) {
        event.preventDefault();
        const target = event.currentTarget;
        const domainUuid = $(target).data('domainUuid');
        if (typeof domainUuid !== 'undefined') {
            window.open('/networking/' + domainUuid + '?'+ 'token=' + this.token, '_blank');
        }
    },

    parseDomainsResultGeneral: function (data) {
        console.log(data);
        this.renderDomainsList(data);
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
