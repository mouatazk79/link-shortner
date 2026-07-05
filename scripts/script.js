import http from "k6/http";
import { check } from "k6";
import { scenario } from "k6/execution";

const BASE_URL = (__ENV.BASE_URL || "http://host.docker.internal:8080").replace(/\/$/, "");
const SHORT_URL_ID = __ENV.SHORT_URL_ID || "dc663c62-c37a-40b7-a5de-a93b5ee676d3";

const RATE = Number(__ENV.RATE || 1000);
const DURATION = __ENV.DURATION || "1m";
const PRE_ALLOCATED_VUS = Number(__ENV.PRE_ALLOCATED_VUS || 250);
const MAX_VUS = Number(__ENV.MAX_VUS || 1000);

const userAgents = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15",
    "Mozilla/5.0 (X11; Linux x86_64; rv:127.0) Gecko/20100101 Firefox/127.0",
    "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
    "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edg/126.0.0.0 Safari/537.36",
];

const referers = [
    "",
    "https://google.com/",
    "https://bing.com/",
    "https://facebook.com/",
    "https://x.com/",
    "https://linkedin.com/",
    "https://example.com/",
];

const languages = [
    "en-US,en;q=0.9",
    "fr-FR,fr;q=0.9,en;q=0.7",
    "es-ES,es;q=0.9,en;q=0.7",
    "de-DE,de;q=0.9,en;q=0.7",
];

const ips = [
    "8.8.8.8",
    "1.1.1.1",
    "41.58.64.1",
    "102.89.23.4",
    "197.210.53.11",
    "154.113.19.8",
];

export const options = {
    scenarios: {
        redirect_load: {
            executor: "constant-arrival-rate",
            rate: RATE,
            timeUnit: "1s",
            duration: DURATION,
            preAllocatedVUs: PRE_ALLOCATED_VUS,
            maxVUs: MAX_VUS,
        },
    },

    thresholds: {
        http_req_failed: ["rate<0.01"],
        http_req_duration: ["p(95)<500"],
    },

    summaryTrendStats: [
        "avg",
        "min",
        "med",
        "p(90)",
        "p(95)",
        "p(99)",
        "max",
    ],
};

function random(array) {
    return array[Math.floor(Math.random() * array.length)];
}

export default function () {
    const headers = {
        "User-Agent": random(userAgents),
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": random(languages),
        "Referer": random(referers),
        "X-Forwarded-For": random(ips),
        "X-Real-IP": random(ips),
        "CF-Connecting-IP": random(ips),
    };

    const url = `${BASE_URL}/shortlinks/${SHORT_URL_ID}`;

    const res = http.get(url, {
        headers,
        redirects: 0, // Measure only your Spring Boot endpoint
        timeout: "10s",
    });

    check(res, {
        "status is redirect": (r) =>
            r.status === 301 ||
            r.status === 302 ||
            r.status === 303 ||
            r.status === 307 ||
            r.status === 308,

        "Location header exists": (r) => !!r.headers.Location,

        "no server error": (r) => r.status < 500,
    });
}