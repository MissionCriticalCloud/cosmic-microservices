'use strict';

const express = require('express');
const fs = require('fs');
const mustache = require('mustache');

const app = express();
app.use('/public', express.static('public'));

app.get('/', function (req, res) {
    fs.readFile('views/index.html', 'utf-8', function (err, data) {
        res.send(mustache.render(data, {
            baseUrl: process.env.COSMIC_USAGE_API_BASE_URL,
            usageUICPUPrice: process.env.COSMIC_USAGE_UI_CPU_PRICE,
            usageUIMemoryPrice: process.env.COSMIC_USAGE_UI_MEMORY_PRICE,
            usageUIStoragePrice: process.env.COSMIC_USAGE_UI_STORAGE_PRICE,
            usageUIIPv4Price: process.env.COSMIC_USAGE_UI_IPV4_PRICE,
            usageUIServiceFee: process.env.COSMIC_USAGE_UI_SERVICE_FEE,
            usageUIInnovationFee: process.env.COSMIC_USAGE_UI_INNOVATION_FEE
        }));
    });
});

const PORT = 8080;

app.listen(PORT, function () {
    console.log('Listening on port: ' + PORT);
});
