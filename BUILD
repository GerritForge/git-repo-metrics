load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_test_util",
    "gerrit_plugin_tests",
)

gerrit_plugin(
    name = "git-repo-metrics",
    srcs = glob(
        ["src/main/java/**/*.java"],
    ),
    manifest_entries = [
        "Gerrit-PluginName: git-repo-metrics",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.gitrepometrics.Module",
        "Gerrit-HttpModule: com.gerritforge.gerrit.plugins.bsl.HttpModule",
        "Implementation-Title: git-repo-metrics plugin",
        "Implementation-URL: https://github.com/GerritForge/git-repo-metrics",
        "Implementation-Vendor: GerritForge",
    ],
    resources = glob(
        ["src/main/resources/**/*"],
    ),
    deps = ["//plugins/gerrit-bsl-license"],
)

gerrit_plugin_test_util(
    name = "git-repo-metrics_tests_lib",
    plugin = "git-repo-metrics",
    srcs = glob(
        ["src/test/java/**/*.java"],
        exclude = [
            "src/test/java/**/*Test.java",
            "src/test/java/**/*IT.java",
        ],
    ),
)

gerrit_plugin_tests(
    plugin = "git-repo-metrics",
    srcs = glob(
        [
            "src/test/java/**/*Test.java",
            "src/test/java/**/*IT.java",
        ],
    ),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":git-repo-metrics_tests_lib",
    ],
)
