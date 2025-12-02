package com.example.kiram.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    // Authentication routes
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Main routes - Tenant
    object TenantHome : Screen("tenant_home")
    object PropertyDetail : Screen("property_detail/{propertyId}") {
        fun createRoute(propertyId: String) = "property_detail/$propertyId"
    }
    object SubmitReview : Screen("submit_review/{userId}/{propertyId}") {
        fun createRoute(userId: String, propertyId: String) = "submit_review/$userId/$propertyId"
    }
    object ReportDamage : Screen("report_damage/{propertyId}") {
        fun createRoute(propertyId: String) = "report_damage/$propertyId"
    }
    object DamageHistory : Screen("damage_history/{propertyId}") {
        fun createRoute(propertyId: String) = "damage_history/$propertyId"
    }
    
    // Main routes - Landlord
    object LandlordHome : Screen("landlord_home")
    object AddProperty : Screen("add_property")
    object EditProperty : Screen("edit_property/{propertyId}") {
        fun createRoute(propertyId: String) = "edit_property/$propertyId"
    }
    object PropertyManagement : Screen("property_management/{propertyId}") {
        fun createRoute(propertyId: String) = "property_management/$propertyId"
    }
    object DisputeManagement : Screen("dispute_management")
    object TenantReview : Screen("tenant_review/{tenantId}/{propertyId}") {
        fun createRoute(tenantId: String, propertyId: String) = "tenant_review/$tenantId/$propertyId"
    }
    
    // Main routes - Manager
    object ManagerHome : Screen("manager_home")
    object CreateAnnouncement : Screen("create_announcement")
    object MaintenanceFees : Screen("maintenance_fees")
    object BuildingIssues : Screen("building_issues")
    
    // Common routes
    object Messages : Screen("messages")
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
