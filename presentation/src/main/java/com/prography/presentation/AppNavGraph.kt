import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prography.auth.route.navigation.LoginRoute
import com.prography.home.route.MainRoute
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.navigation.OnboardingRoute
import com.prography.organize.navigation.OrganizeRoute
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
                is NavigationEvent.TopLevelTo -> { /* 탭 이동 처리 */ }
                is NavigationEvent.BottomBarTo -> { /* 바텀 이동 처리 */ }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable<AppRoute.Onboarding> {
            OnboardingRoute(navigationHelper = navigationHelper)
        }
        composable<AppRoute.Login> {
            LoginRoute(navigationHelper = navigationHelper)
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
    }
}
