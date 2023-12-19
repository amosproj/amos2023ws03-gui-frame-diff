# Generating license reports for our dependencies

The project uses a [gradle plugin](https://github.com/jk1/Gradle-License-Report) to generate dependency license reports.

The reports are all saved in the `./licenses/reports/<SubprojectName>` directories.
Currently, we are allowing the licenses `MIT` and `Apache 2.0`. Furthermore, the plugin
has difficulties (see this [issue](https://github.com/jk1/Gradle-License-Report/issues/272))
recognizing licenses for jetbrains packages (`compose`, `skiko`, `kotlinx`),
which are all under the `Apache 2.0` license. Thus, we can safely white-flag them.

This information can be changed in `./licenses/allowed-licenses.json`.
