# Project-specific R8 rules.
#
# Do NOT add blanket rules such as `-keep class androidx.compose.** { *; }`.
# AndroidX (Compose, Room, WorkManager, Navigation, DataStore), Firebase and Lottie
# ship their own consumer rules. Blanket keeps stop R8 from removing unused code
# (for example every icon in material-icons-extended) and roughly double the download.
# If R8 reports missing classes, add only the specific lines it writes to
# app/build/outputs/mapping/release/missing_rules.txt.

# Keep line numbers so Crashlytics stack traces stay readable (the Crashlytics
# Gradle plugin uploads the mapping file), and hide the original file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
