# Vert.x Websocket Race Condition

A minimal reproduction that demonstrates a race condition in the handling of Websockets with Vert.x 5.

## Reproduction

Run: `./gradlew build`.

The `Vert.x 4` integration test will (hopefully!) pass, whilst the `Vert.x 5` version will fail.

## Description

The `Vert.x 4` and `Vert.x 5` modules are almost identical; the only difference is how they accept a `ServerWebSocket`.

In the case of `Vert.x 4`, the `ServerWebSocket` can be accepted using the (deprecated) `ServerWebSocket#accept()`.
In `Vert.x 5` this method has been removed, forcing the use of `ServerWebSocketHandshake#accept()` instead.

The problem is that `Vert.x 5` forces the handshake to be completed before the `ServerWebSocket` is available.
This prevents performing necessary set-up, such as attaching `Handler`s.
This could lead to a race condition as reproduced in the integration test.

In this particular example, the "problem" is caused by the logging in the `WebsocketVerticle#connect()` method.
It's somewhat contrived, but still slightly nicer than just sticking in a `LockSupport.parkNanos()` or similar.
In practice, the problem might be encountered because of:

- logging, as shown here
- class-loading
- other blocking or asynchronous calls that need to be complete before attaching a handler
