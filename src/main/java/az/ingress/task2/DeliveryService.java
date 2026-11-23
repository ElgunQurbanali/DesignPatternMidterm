package az.ingress.task2;
/*Factory Pattern*/

import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static az.ingress.task2.DeliveryService.DeliveryType.*;

/**
 * Sifarişin çatdırılma qiymətinin hesablanması.
 */
public class DeliveryService {

    record Order(String customer, double total, double weightKg, DeliveryType deliveryType) {
    }

    /*
     * TODO:
     *  1. Yeni çatdırılma növü SAME_DAY: 15 AZN, 0 gün, ad "Same day".
     *  2. Yeni çatdırılma növü LOCKER: 3 AZN, sifariş 50 AZN və çoxdursa pulsuz, 2 gün, ad "Parcel locker".
     *  3. Hər növün qiyməti, müddəti və adı bir yerdə olsun. Yeni növ əlavə edəndə
     *     price(), days(), label() metodlarını dəyişmək lazım olmasın.
     *  4. Növ API-dən String kimi gəlir. String-ə görə növü seçmək bir yerdə olsun.
     *     Naməlum növ -> "Unknown delivery type: X"
     */

    interface DeliveryStrategy {
        double price(Order order);
        int days();
        String label();
    }


    static class CourierDelivery implements DeliveryStrategy {
        public double price(Order o) { return o.total() >= 100 ? 0 : 5 + o.weightKg() * 0.5; }
        public int days()            { return 2; }
        public String label()        { return "Courier"; }
    }

    static class PickupDelivery implements DeliveryStrategy {
        public double price(Order o) { return 2; }
        public int days()            { return 3; }
        public String label()        { return "Pickup point"; }
    }

    static class ExpressDelivery implements DeliveryStrategy {
        public double price(Order o) { return 10 + o.weightKg(); }
        public int days()            { return 1; }
        public String label()        { return "Express"; }
    }

    static class SameDayDelivery implements DeliveryStrategy {
        public double price(Order o) { return 15; }
        public int days()            { return 0; }
        public String label()        { return "Same day"; }
    }

    static class LockerDelivery implements DeliveryStrategy {
        public double price(Order o) { return o.total() >= 50 ? 0 : 3; }
        public int days()            { return 2; }
        public String label()        { return "Parcel locker"; }
    }



    static class DeliveryStrategyFactory {
        private static final Map< DeliveryType, DeliveryStrategy> STRATEGIES = Map.of(
                COURIER,  new CourierDelivery(),
                PICKUP,   new PickupDelivery(),
                EXPRESS,  new ExpressDelivery(),
                SAME_DAY, new SameDayDelivery(),
                LOCKER,   new LockerDelivery()
        );

        static DeliveryStrategy get(String type) {
            DeliveryStrategy strategy = type == null ? null : STRATEGIES.get(type);
            if (strategy == null) {
                throw new IllegalArgumentException("Unknown delivery type: " + type);
            }
            return strategy;
        }
    }


    enum DeliveryType{
        COURIER,
        PICKUP,
        EXPRESS,
        SAME_DAY,
        LOCKER
    }


    public double price(Order order) {
        switch (order.deliveryType()) {
            case COURIER:
                if (order.total() >= 100) {
                    return 0;
                }
                return 5 + order.weightKg() * 0.5;
            case PICKUP:
                return 2;
            case EXPRESS:
                return 10 + order.weightKg();
            default:
                throw new IllegalArgumentException("Unknown delivery type: " + order.deliveryType());
        }
    }


    public int days(Order order) {
        switch (order.deliveryType()) {
            case COURIER:
                return 2;
            case PICKUP:
                return 3;
            case EXPRESS:
                return 1;
            case SAME_DAY:
                return 0;
            case LOCKER:
                if (order.total>=50) {return 0;}
                else {return 2;}

            default:
                throw new IllegalArgumentException("Unknown delivery type: " + order.deliveryType());
        }
    }

    public String label(Order order) {
        switch (order.deliveryType()) {
            case COURIER:
                return "Courier";
            case PICKUP:
                return "Pickup point";
            case EXPRESS:
                return "Express";
            case SAME_DAY:
                return "Same day";
            case LOCKER:
                return "Locker";
            default:
                throw new IllegalArgumentException("Unknown delivery type: " + order.deliveryType());
        }
    }

    public String receipt(Order order) {
        return String.format("%s: %s, %.2f AZN, %d day(s)",
                order.customer(), label(order), price(order), days(order));
    }

    public static void main(String[] args) {
        DeliveryService service = new DeliveryService();

        List<Order> orders = List.of(
                new Order("Aysel", 120, 3, COURIER),
                new Order("Murad", 40, 2, COURIER),
                new Order("Leyla", 30, 1, PICKUP),
                new Order("Kamran", 80, 4, EXPRESS),
                new Order("Nigar", 60, 1, SAME_DAY),
                new Order("Rashad", 70, 2, LOCKER),
                new Order("Elvin", 20, 1, LOCKER),
                new Order("Farid", 50, 1, SAME_DAY)
        );

        for (Order order : orders) {
            try {
                System.out.println(service.receipt(order));
            } catch (IllegalArgumentException e) {
                System.out.println(order.customer() + ": " + e.getMessage());
            }
        }
    }
}
