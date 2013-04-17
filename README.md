# Peddle

The Peddle API is a finagle server for bitcoin. Deploying Peddle makes available an api endpoint where you can issue GET and POST requests to update your e-wallet.  All responses will be JSON-formatted, convential HTTP response codes are used.  Currently there is no user authentication.

## Prerequisites

You need to have bitcoind installed and the environment variables correct

On Mac OS X via <https://bitcointalk.org/index.php?topic=133097.0>

```
  brew tap WyseNynja/bitcoin
  brew install bitcoind
```

## Building Peddle
```
  sbt clean compile stage
  target/start
```

## TESTS

```
  sbt tests
```