public class OrderLifecycleManagement {
    public static void main(String[] args) throws InvalidOperationException {

        Order order = new Order();

        order.pay();
        order.ship();
        order.deliver();

        System.out.println("--- Attempting invalid transition ---");
        try {
            order.cancel();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n--- Second scenario ---");

        Order canceled = new Order();
        canceled.pay();
        canceled.cancel();


        try {
            canceled.ship();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static class Order {
        private IState state;

        Order() {
            state = Start.getInstance();
        }

        void pay() throws InvalidOperationException {
            state = state.pay();
        }

        void cancel() throws InvalidOperationException {
            state = state.cancel();
        }

        void ship() throws InvalidOperationException {
            state = state.ship();
        }

        void deliver() throws InvalidOperationException {
            state = state.deliver();
        }
    }

    static class InvalidOperationException extends Exception {
        public InvalidOperationException(String message) {
            super(message);
        }
    }

    interface IState {
        IState pay() throws InvalidOperationException;

        IState cancel() throws InvalidOperationException;

        IState ship() throws InvalidOperationException;

        IState deliver() throws InvalidOperationException;
    }

    static class Start implements IState {
        private static final IState INSTANCE = new Start();

        public static IState getInstance() {
            return INSTANCE;
        }

        @Override
        public IState pay() {
            return Paid.getInstance();
        }

        @Override
        public IState cancel() {
            return Cancelled.getInstance();
        }

        @Override
        public IState ship() throws InvalidOperationException {
            throw new InvalidOperationException("The order can not be shipped if it has not been Paid");
        }

        @Override
        public IState deliver() throws InvalidOperationException {
            throw new InvalidOperationException("The order can not be delivered if it has not been shipped");
        }
    }

    static class Paid implements IState {
        private static final IState INSTANCE = new Paid();

        public static IState getInstance() {
            return INSTANCE;
        }

        @Override
        public IState pay() throws InvalidOperationException {
            throw new InvalidOperationException("The order has already been Paid");
        }

        @Override
        public IState cancel() {
            return Cancelled.getInstance();
        }

        @Override
        public IState ship() {
            return Shipped.getInstance();
        }

        @Override
        public IState deliver() throws InvalidOperationException {
            throw new InvalidOperationException("The order can not be delivered if it has not been shipped");
        }
    }

    static class Shipped implements IState {
        private static final IState INSTANCE = new Shipped();

        public static IState getInstance() {
            return INSTANCE;
        }


        @Override
        public IState pay() throws InvalidOperationException {
            throw new InvalidOperationException("The order has already been Paid");
        }

        @Override
        public IState cancel() throws InvalidOperationException {
            throw new InvalidOperationException("Cannot cancel shipped orders");
        }

        @Override
        public IState ship() throws InvalidOperationException {
            throw new InvalidOperationException("The order has already been shipped");
        }

        @Override
        public IState deliver() {
            return Completed.getInstance();
        }
    }

    static class Completed implements IState {
        private static final IState INSTANCE = new Completed();

        public static IState getInstance() {
            return INSTANCE;
        }

        @Override
        public IState pay() throws InvalidOperationException {
            throw new InvalidOperationException("The order is completed");
        }

        @Override
        public IState cancel() throws InvalidOperationException {
            throw new InvalidOperationException("The order is completed");
        }

        @Override
        public IState ship() throws InvalidOperationException {
            throw new InvalidOperationException("The order is completed");
        }

        @Override
        public IState deliver() throws InvalidOperationException {
            throw new InvalidOperationException("The order is completed");
        }
    }

    static class Cancelled implements IState {
        private static final IState INSTANCE = new Cancelled();

        public static IState getInstance() {
            return INSTANCE;
        }

        @Override
        public IState pay() throws InvalidOperationException {
            throw new InvalidOperationException("The order is canceled");
        }

        @Override
        public IState cancel() throws InvalidOperationException {
            throw new InvalidOperationException("The order is canceled");
        }

        @Override
        public IState ship() throws InvalidOperationException {
            throw new InvalidOperationException("The order is canceled");
        }

        @Override
        public IState deliver() throws InvalidOperationException {
            throw new InvalidOperationException("The order is canceled");
        }
    }

}