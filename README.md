# PDFTron Android Samples
This repository contains sample projects using different features of the PDFTron Android SDK. The guides for each sample can be found in the [PDFTron Android documentation](https://www.pdftron.com/documentation/android).

## Integration samples
| Sample | Description |
|--|--|
|[StandardPDFViewer](./StandardPDFViewer)| Standard gradle integration as described [in this guide](https://www.pdftron.com/documentation/android/guides/getting-started/integrate-gradle)
|[PDFViewCtrlViewer](./PDFViewCtrlViewer)| Viewer integration using PDFViewCtrl and AnnotationToolbar|

## Customization samples
| Sample | Description |
|--|--|
|[CustomStickyNote](./CustomStickyNote)| Customize sticky note icons as described [in this guide](https://www.pdftron.com/documentation/android/guides/advanced/customize-color-picker#customize-the-icon-picker)
|[CustomQuickMenu](./CustomQuickMenu)| Customize the quick menu icons as described [in this guide](https://www.pdftron.com/documentation/android/guides/advanced/customize-quick-menu)

## Collaboration samples
| Sample | Description |
|--|--|
|[CollabFirebaseSample](./CollabFirebaseSample)| Create a real-time document collaboration app as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-1) and customize as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-2) |

## Running the samples

1. **Import one of the sample projects into Android Studio**

2. **Add your PDFTron license key**

	If you do not already have a valid PDFTron license key, please [contact sales](https://www.pdftron.com/form/contact-sales) for a commercial license key or click [here](https://www.pdftron.com/documentation/android/guides/getting-started/add-license) to get an evaluation key.

	Add your [PDFTron license key and credentials](https://www.pdftron.com/documentation/android/guides/getting-started/integrate-gradle) to the `gradle.properties` file:
	```
	AWS_ACCESS_KEY=YOUR_ACCESS_KEY_GOES_HERE
	AWS_SECRET_KEY=YOUR_SECRET_KEY_GOES_HERE
	PDFTRON_LICENSE_KEY=YOUR_PDFTRON_LICENSE_KEY_GOES_HERE
	```

	Your PDFTron license key and credentials are confidential. Please make sure that they are not publicly available.

3. **Run the project in Android Studio**

## Contributing
See [Contributing](./CONTRIBUTING.md)

## License
See [License](./LICENSE)
