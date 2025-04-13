class Hello {
    public static void main(String[] args) {
        System.out.println("hello");
        Callee callee = new Callee();
        callee.call("world");
    }
}
