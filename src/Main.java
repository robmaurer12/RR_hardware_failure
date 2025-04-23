public class Main {

    public static void main(String[] args) {

        // These are hard coded P1 and P2 values
        Object[][] Ready_Queue = {
                {"P1", "A", 10},
                {"P2", "B", 7}
        };

        int Time_Quantum = 4; // This is the hard coded Time quantum value
        int tick = 0; // Initial tick starts at 0
        int Ready_Queue_row_index = -1; // Initial Ready_Queue_row_index starts at -1
        String bottom_numbers = "0";
        String process_order = "|";
        int periodic_signal = 3;
        int addtonext = 0;
        Object[][] Signal_list = {
        };
        while (sumremainingtimes(Ready_Queue) > 0) {
            int Quantum_start_tick = tick;
            int Quantum_end_tick = tick + Time_Quantum;

            // This gets the finds if there are A,B,C processes and gets there first index
            int[] next_index = searchforABCindex(Ready_Queue);

            // next_index[2] = C so if C is in the ready queue do that first
            if (next_index[2] != -1) {
                Ready_Queue_row_index = next_index[2];
            } else if (next_index[0] != -1) {
                Ready_Queue_row_index = next_index[0];
            } else if (next_index[1] != -1) {
                Ready_Queue_row_index = next_index[1];
            }
            String processID = (String) Ready_Queue[Ready_Queue_row_index][0];
            int processremainingtime = (int) Ready_Queue[Ready_Queue_row_index][2];
            String processletter = (String) Ready_Queue[Ready_Queue_row_index][1];
            for (int i = Quantum_start_tick; i < Quantum_end_tick; i++) {
                if (processremainingtime > 0){
                    // Hardware Failure signals
                    if (tick != 0){
                        if (processremainingtime != 0 || addtonext == 1 ){
                            if (tick % periodic_signal == 0){
                                Signal_list = addtoSignal_list(Signal_list,processID);
                                addtonext = 0;
                            }
                        } else {
                            addtonext = 1;
                        }
                    }
                    tick++;
                    processremainingtime--;

                    if (processletter.equals("A") && (processremainingtime == 7 || processremainingtime == 4 || processremainingtime == 1)) {
                        //adds B process if process A is at 3,6,or 9
                        Ready_Queue = addProcessToReadyQueue(Ready_Queue, "B", 7);
                    }
                    if (processletter.equals("B") && (processremainingtime == 4 || processremainingtime == 1)) {
                        //adds C process if process B is at 3,6
                        Ready_Queue = addProcessToReadyQueue(Ready_Queue, "C", 5);
                    }


                }
            }
            Ready_Queue[Ready_Queue_row_index][2] = processremainingtime;
            // this is just formating for gantt chart
            if (tick > 10 &&
                    ((String) Ready_Queue[Ready_Queue_row_index][0]).length() < 3){
                bottom_numbers = bottom_numbers + " " + tick;
            } else if (((String) Ready_Queue[Ready_Queue_row_index][0]).length() == 3) {
                bottom_numbers = bottom_numbers + "  " + tick;
            } else {
                bottom_numbers = bottom_numbers + "  " + tick;
            }

            process_order = process_order + "" + Ready_Queue[Ready_Queue_row_index][0] + "|";
        }
        System.out.print("\nA.)\nGantt Chart:\n");
        System.out.println(process_order);
        System.out.println(bottom_numbers);
        System.out.print("\nB.)\nSignals Received By Each Process:\n");


        Signal_list = sortSignalList(Signal_list);

        for (int i = 0; i < Signal_list.length; i++) {
            System.out.println(Signal_list[i][0] + " received signal " + Signal_list[i][1] + " times.");
        }
    }
    public static Object[][] addProcessToReadyQueue(Object[][] readyQueue, String priority, int remainingTime) {
        // Create a new array with an additional slot for the new process
        Object[][] newReadyQueue = new Object[readyQueue.length + 1][3];

        int numberOfRows = readyQueue.length;
        String lastprocessname = (String) readyQueue[numberOfRows - 1][0];
        String result = lastprocessname.replace('P', '0');
        int number = Integer.parseInt(result) + 1;

        // Copy existing elements to the new array
        for (int i = 0; i < readyQueue.length; i++) {
            newReadyQueue[i] = readyQueue[i];
        }

        // Add new process at the end
        newReadyQueue[readyQueue.length] = new Object[]{"P" + number, priority, remainingTime};
        return newReadyQueue; // Return the updated readyQueue
    }
    public static int[] searchforABCindex(Object[][] queue) {
        // Initialize the indices to -1 (meaning not found)
        int[] result = {-1, -1, -1}; // Array to store indices of "A", "B", "C"

        // Iterate over the array to find the first occurrence of A, B, and C
        for (int i = 0; i < queue.length; i++) {
            if (queue[i][1] instanceof String && queue[i][2] instanceof Integer) {
                String value = (String) queue[i][1];
                Integer num = (Integer) queue[i][2];

                if (num != 0) {
                    switch (value) {
                        case "A":
                            if (result[0] == -1) result[0] = i; break;
                        case "B":
                            if (result[1] == -1) result[1] = i; break;
                        case "C":
                            if (result[2] == -1) result[2] = i; break;
                    }
                }
            }
            // Stop if all have been found
            if (result[0] != -1 && result[1] != -1 && result[2] != -1) {
                break;
            }
        }
        return result;
    }
    public static int sumremainingtimes(Object[][] readyQueue) {
        int sum = 0;
        for (int i = 0; i < readyQueue.length; i++) {
            sum += (int) readyQueue[i][2]; // Add the remaining time of each process
        }
        return sum;
    }
    public static Object[][] addtoSignal_list(Object[][] Signal_list, String input) {
        // Flag to check if the input already exists in the Signal_list
        boolean found = false;

        // Loop through the Signal_list to check if the input matches any signal
        for (int i = 0; i < Signal_list.length; i++) {
            if (Signal_list[i][0].equals(input)) {
                // If input matches, increment the value in the second column
                Signal_list[i][1] = (int) Signal_list[i][1] + 1;
                found = true;
                break;
            }
        }

        // If input doesn't match any existing signal, add a new row
        if (!found) {
            // Create a new Signal_list array with an additional row
            Object[][] newSignalList = new Object[Signal_list.length + 1][2];
            System.arraycopy(Signal_list, 0, newSignalList, 0, Signal_list.length);
            newSignalList[Signal_list.length] = new Object[]{input, 1};
            Signal_list = newSignalList;
        }
        return Signal_list; // Return the updated Signal_list
    }
    public static Object[][] sortSignalList(Object[][] signalList) {
        boolean swapped;
        for (int i = 0; i < signalList.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < signalList.length - i - 1; j++) {
                String processID1 = (String) signalList[j][0];
                String processID2 = (String) signalList[j + 1][0];
                int id1 = Integer.parseInt(processID1.replace("P", ""));
                int id2 = Integer.parseInt(processID2.replace("P", ""));

                // If the current process ID is greater than the next one, swap
                if (id1 > id2) {
                    Object[] temp = signalList[j];
                    signalList[j] = signalList[j + 1];
                    signalList[j + 1] = temp;
                    swapped = true;
                }
            }
            // If no two elements were swapped, the list is sorted
            if (!swapped) {
                break;
            }
        }
        return signalList;
    }
}