# Toss Widget

A minimalist native Android coin toss app with a home screen widget.

## The Trick
The result of the toss is determined by your device's **Media Volume**:
- **100% Volume**: Always HEADS.
- **0% Volume**: Always TAILS.
- **Anything in between**: A fair 50/50 toss.

## How to Build (via GitHub)
Since you don't have Android Studio, use GitHub Actions to build the APK:
1. Push this code to a GitHub repository.
2. Go to the **Actions** tab in your repository.
3. Select the **Build APK** workflow.
4. If it hasn't run automatically, you can trigger it via `workflow_dispatch`.
5. Once finished, click on the successful run to find the `app-debug` artifact.
6. Download the zip, extract it, and install the APK on your Android device.

## How to Use
1. Install the APK.
2. Open the app once to see the instructions.
3. Go to your home screen, long-press, and add the **Toss Widget** (usually 1x1).
4. Tap the widget to "expand" it and perform a toss.
5. Tap "Toss Again" or the coin to repeat.
