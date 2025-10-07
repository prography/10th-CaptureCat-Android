import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.android.start.StartRoute
import com.prography.auth.route.navigation.LoginRoute
import com.prography.home.route.MainRoute
import com.prography.imageDetail.ui.route.ImageDetailRoute
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.navigation.InitOnboardingRoute
import com.prography.onboarding.navigation.OnboardingRoute
import com.prography.organize.navigation.OrganizeRoute
import com.prography.setting.route.SettingRoute
import com.prography.setting.route.WithdrawRoute
import com.prography.home.ui.home.upload.UploadRoute
import com.prography.util.MixpanelUtil
import com.prography.util.permission.ScreenshotPermissionGate
import kotlinx.coroutines.flow.collectLatest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.prography.home.ui.storage.screen.SelectPictureScreen
import com.prography.tag.navigation.TagSettingRoute

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    navigationHelper: NavigationHelper,
    startDestination: AppRoute = AppRoute.Onboarding
) {

    LaunchedEffect(Unit) {
        navigationHelper.navigationFlow.collectLatest { event ->
            when (event) {
                is NavigationEvent.To -> {
                    navController.navigate(event.route) {
                        if (event.popUpTo) popUpTo(0) { inclusive = true }
                    }
                }

                is NavigationEvent.Up -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController, startDestination = startDestination,
        enterTransition = { slideInHorizontally { it }  },
        popEnterTransition = { slideInHorizontally { -it } }) {
        composable<AppRoute.InitOnboarding> {
            InitOnboardingRoute(navigationHelper = navigationHelper)
        }

        composable<AppRoute.Onboarding> {
            OnboardingRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Login> {
            LoginRoute()
        }
        composable<AppRoute.Start> {
            StartRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.StartTag> {
            com.android.start.StartTagScreen(
                onFinishSelection = { selectedTags ->
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.StartPermission))
                },
                onNavigateBack = {
                    navigationHelper.navigate(NavigationEvent.Up)
                }
            )
        }
        composable<AppRoute.StartPermission> {
            com.android.start.StartPermissionScreen(
                onNext = {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.StartChoose))
                }
            )
        }
        composable<AppRoute.StartChoose> {
            val context = LocalContext.current

            ScreenshotPermissionGate(
                onPermissionGranted = {
                    MixpanelUtil.track("view_start_inbox")
                    com.android.start.StartChooseScreen(
                        maxSelectableImages = 10,
                        onFinishSelection = { selectedImages ->
                            navigationHelper.navigate(
                                NavigationEvent.To(
                                    AppRoute.Organize(
                                        screenshotIds = selectedImages.map { it.id },
                                        entryPoint = "start_inbox"
                                    )
                                )
                            )
                        }
                    )
                },
                onNavigateToSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            )
        }
        composable<AppRoute.Upload> {
            UploadRoute()
        }
        composable<AppRoute.Main> {
            MainRoute()
        }
        composable<AppRoute.MyPage> {
            SettingRoute()
        }
        composable<AppRoute.Storage> { backStackEntry ->
            val storage = backStackEntry.toRoute<AppRoute.Storage>()
            SelectPictureScreen(storage.mode)
        }

        composable<AppRoute.Organize> { backStackEntry ->
            val organize = backStackEntry.toRoute<AppRoute.Organize>()
            OrganizeRoute(
                navigationHelper = navigationHelper,
                screenshotIds = organize.screenshotIds,
                entryPoint = organize.entryPoint
            )
        }
        composable<AppRoute.ImageDetail> { backStackEntry ->
            val imageDetail = backStackEntry.toRoute<AppRoute.ImageDetail>()
            ImageDetailRoute(
                navigationHelper = navigationHelper,
                navController = navController,
                screenshotIds = imageDetail.screenshotIds,
                currentIndex = imageDetail.currentIndex,
                entryPoint = imageDetail.entryPoint
            )
        }
        composable<AppRoute.SettingRoute.Setting> {
            SettingRoute()
        }
        composable<AppRoute.SettingRoute.Withdraw> {
            WithdrawRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.TagSetting> {
            TagSettingRoute()
        }
    }
}
