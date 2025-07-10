import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.android.start.StartRoute
import com.android.start.ScreenshotItem
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
import com.prography.favorite.ui.route.FavoriteRoute
import kotlinx.coroutines.flow.collectLatest

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

    NavHost(navController = navController, startDestination = startDestination) {
        composable<AppRoute.InitOnboarding> {
            InitOnboardingRoute(navigationHelper = navigationHelper)
        }

        composable<AppRoute.Onboarding> {
            OnboardingRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Login> {
            LoginRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Start> {
            StartRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Main> {
            MainRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Organize> { backStackEntry ->
            val organize = backStackEntry.toRoute<AppRoute.Organize>()
            OrganizeRoute(
                navigationHelper = navigationHelper,
                screenshotIds = organize.screenshotIds
            )
        }
        composable<AppRoute.ImageDetail> { backStackEntry ->
            val imageDetail = backStackEntry.toRoute<AppRoute.ImageDetail>()
            ImageDetailRoute(
                navigationHelper = navigationHelper,
                screenshotIds = imageDetail.screenshotIds,
                currentIndex = imageDetail.currentIndex
            )
        }
        composable<AppRoute.Favorite> {
            FavoriteRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.SettingRoute.Setting> {
            SettingRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.SettingRoute.Withdraw> {
            WithdrawRoute(navigationHelper = navigationHelper)
        }
    }
}
