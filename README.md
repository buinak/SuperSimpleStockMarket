# SuperSimpleStockMarket
## Description
This project is the deliverable for the take-home assignment by JP Morgan -- "Super Simple 
Stock Market".

The PDF describing the precise requirements is bundled together and can be found in the root
folder of the project.

### Assumptions

Stock ticker price is calculated in a simple manner -- stock data service will return either 
the last price at which a stock was traded, or, if none, the par value.

Fractional trading is allowed -- so quantity of trades is represented by a decimal number
rather than an integer.

The default precision for decimals is 4, and the rounding is HALF-UP for all calculations.

### Implementation Comments

Tests are limited -- I added tests for the main requirements -- so for the ComputeService --
to test them. Normally, all services would have their separate unit/integration tests.
Tests use sample data provided whenever possible.

No logging done -- but for a real application would be a must.

I have included some libraries, such as Lombok, that I also use in my regular work tasks.

The packaging style here chosen is the "package-by-layer", which would be suitable given the
size of the project. However, the style I prefer is "package-by-component", which would then
be packaged by layer inside a given feature. This is difficult to showcase in a small project,
so I chose to go with this style.