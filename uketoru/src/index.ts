import "./logger";

import fastify from "fastify";
import * as toml from "toml";

import * as fs from "node:fs";
import * as path from "node:path";
import { AMQPClient } from "@cloudamqp/amqp-client";

const CONFIG_FILE = "uketoru.toml";
const CONFIG_PATH = process.cwd() + path.sep + CONFIG_FILE;
if (!fs.existsSync(CONFIG_PATH)) {
    throw `File '${CONFIG_FILE}' was not found, it is required for configuration.`;
}

try {
    fs.accessSync(CONFIG_PATH, fs.constants.R_OK);
} catch (e) {
    throw `File '${CONFIG_FILE}' is not readable.`;
}

const server = fastify();
const { uketoru: config } = toml.parse(fs.readFileSync(CONFIG_PATH, "utf8"));
const amqp = new AMQPClient(config.amqp.uri)

async function main() {
    await amqp.connect();
    console.info(`[amqp] now connected to ${config.amqp.uri}`);

    server.get("/:type", async (req, res) => {
        const { type } = req.params as { type: string };
        console.log(type);

        res.status(200).send()
    });

    server.listen(config.server.port, (err, address) => {
        if (err) {
            console.error(`[server] error while starting server`, err);
            process.exit(1);
        }

        console.info(`[server] listening on ${address}`);
    });
}

void main();
