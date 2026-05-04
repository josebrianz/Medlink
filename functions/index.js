const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.onStockUpdate = functions.firestore
    .document('inventory/{pharmacyId}/drugs/{drugId}')
    .onUpdate(async (change, context) => {
        const newValue = change.after.data();
        const previousValue = change.before.data();

        // ONLY trigger if stock was 0 or "Out of Stock" and is now > 0
        if ((!previousValue.inStock || previousValue.quantity === 0) && newValue.quantity > 0) {

            // 1. Find all active subscriptions for this drug at this pharmacy
            const subsSnapshot = await admin.firestore().collection('subscriptions')
                .where('pharmacyId', '==', context.params.pharmacyId)
                .where('drugId', '==', context.params.drugId)
                .where('isActive', '==', true)
                .get();

            if (subsSnapshot.empty) {
                console.log('No active subscriptions found.');
                return null;
            }

            // 2. Send notifications to each subscribed user
            const notificationPromises = subsSnapshot.docs.map(async (doc) => {
                const subscriptionData = doc.data();
                const userId = subscriptionData.userId;

                // Get the user's FCM token from their profile
                const userDoc = await admin.firestore().collection('users').doc(userId).get();
                const fcmToken = userDoc.data()?.fcmToken;

                if (fcmToken) {
                    const message = {
                        token: fcmToken,
                        notification: {
                            title: "Drug Available! 🏥",
                            body: `${newValue.name} is now back in stock at ${newValue.pharmacyName || 'your pharmacy'}.`
                        },
                        data: {
                            drugId: context.params.drugId,
                            pharmacyId: context.params.pharmacyId,
                            click_action: "FLUTTER_NOTIFICATION_CLICK" // Standard for many apps
                        }
                    };
                    return admin.messaging().send(message);
                }
            });

            return Promise.all(notificationPromises);
        }
        return null;
    });