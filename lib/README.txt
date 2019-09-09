Place for third party libraries required for running unit tests, which are not free and not available in Maven cental.
For example oracle jdbc drivers.
All jars in this directory are git ignored.
All unit tests dependend on libraries must be marked with @Ignore, to be excluded from standard test suites.
Unit test may be still executed manually from IDE.