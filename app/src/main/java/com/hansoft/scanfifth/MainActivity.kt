package com.hansoft.scanfifth

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider


import com.hansoft.scanfifth.ui.theme.ScanFifthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ScanFifthTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val people = People("John Doe",20)
    val chat = Chat("Tom","Hello")
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imageUri = null // Reset image URI when permission is granted
        }
    }
    val context = LocalContext.current
    //val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }
    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val previewView = remember { PreviewView(context) }
    val cameraProvider = cameraProviderFuture.get()
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val preview = androidx.camera.core.Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            //.background(color = Color.White)
    ) {
        val (viewfinder, srcLang, srcTextScrollView, divider, targetLangSelector, translatedTextScrollView, divider2, imageView, progressBar, progressText) = createRefs()
        val backgroundColor = MaterialTheme.colorScheme.background
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center,
        ) {

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as ComponentActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                Log.d("aaa", "CameraPreview: eeefff")
            } catch (exc: Exception) {
                Log.e("aaa", "Use case binding failed", exc)
            }

            // Camera Preview View



                    Box(
                        modifier = Modifier
                            .padding(top = 100.dp)
                            .width(250.dp)
                            .height(150.dp) // Adjust this as needed
                        //.padding(top = 20.dp)
                    ) {
                        //Text(text = "hello")
                        //Spacer(modifier = Modifier.height(30.dp))
                        AndroidView(
                            // factory = { context -> PreviewView(context).apply {
                            //  setBackgroundColor(android.graphics.Color.TRANSPARENT) // Explicitly set transparency
                            // } },
                            factory = {
                                previewView.apply {
                                    //  setBackgroundColor(android.graphics.Color.TRANSPARENT) // Explicitly set transparency
                                }


                            },
                            modifier = Modifier
                                //.padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 50.dp)
                                .width(150.dp)
                                .height(20.dp)
                                .rotate(90.0f)
                            //.align(Alignment.Center)


                        )



                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // Adjust the overlay length here
                            //.align(Alignment.Center)
                            .background(color = backgroundColor) // Semi-transparent overlay
                    ) {
                        Text(
                            text = "good morning",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }





            }



        }

                Log.d("aaa", "MainScreen: viewfinder = ${viewfinder.top}")

                // Overlay SurfaceView







            // Middle Guideline
            val guideline = createGuidelineFromTop(0.5f)



            // Source Language TextVie
            Text(text = people.name + " " + people.age)
            Text(text = chat.sender + " " + chat.message)

            Text(
                text = "source lang",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .constrainAs(srcLang) {
                        top.linkTo(guideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(srcTextScrollView.top)
                    }
            )

            // Source Text ScrollView
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .verticalScroll(rememberScrollState())
                    .constrainAs(srcTextScrollView) {
                        top.linkTo(srcLang.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(divider.top)
                    }
            ) {
                Text(
                    text = "source text",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Divider
        HorizontalDivider(modifier = Modifier
            .constrainAs(divider) {
                top.linkTo(srcTextScrollView.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(targetLangSelector.top)
            }
        )

            // Target Language Selector (Spinner equivalent)
            AndroidView(
                factory = { context -> Spinner(context) },
                modifier = Modifier.constrainAs(targetLangSelector) {
                    top.linkTo(divider.bottom)
                    start.linkTo(parent.start, 20.dp)
                    bottom.linkTo(translatedTextScrollView.top)
                }
            )

            // Translated Text ScrollView
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .verticalScroll(rememberScrollState())
                    .constrainAs(translatedTextScrollView) {
                        top.linkTo(targetLangSelector.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(divider2.top)
                    }
            ) {
                Text(
                    text = "translated text",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Divider 2
        HorizontalDivider(modifier = Modifier
            .constrainAs(divider2) {
                top.linkTo(translatedTextScrollView.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(imageView.top)
            }
        )

            // ImageView
            Image(
                painter = painterResource(id = R.drawable.greyscale_regular_3x),
                contentDescription = stringResource(id = R.string.content_description_google_translate_attribution),
                modifier = Modifier
                    .size(18.dp)
                    .constrainAs(imageView) {
                        top.linkTo(divider2.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )



        // Progress Bar
        CircularProgressIndicator(
            modifier = Modifier
                .size(30.dp)
                .constrainAs(progressBar) {
                    start.linkTo(targetLangSelector.end)
                    top.linkTo(targetLangSelector.top)
                    bottom.linkTo(targetLangSelector.bottom)
                },
            //visibility = ProgressIndicatorVisibility.INVISIBLE
        )



            // Progress Text
            Text(
                text = stringResource(id = R.string.downloading_model_files),
                color = Color.White,
                modifier = Modifier.constrainAs(progressText) {
                    start.linkTo(progressBar.end)
                    top.linkTo(progressBar.top)
                    bottom.linkTo(progressBar.bottom)
                }
            )



    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column {


        Text(
            text = "Hello",
            modifier = modifier
        )
        Text(
            text = "Hi",
            modifier = modifier
        )
        MainScreen(modifier = modifier)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScanFifthTheme {
        Greeting()
    }
}