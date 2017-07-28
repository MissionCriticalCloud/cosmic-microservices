#!/bin/sh

vault write secret/cosmic-bill-viewer @/missioncriticalcloud/cosmic-bill-viewer.json
vault write secret/cosmic-metrics-collector @/missioncriticalcloud/cosmic-metrics-collector.json
vault write secret/cosmic-usage-api @/missioncriticalcloud/cosmic-usage-api.json
