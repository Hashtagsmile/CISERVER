public class NegComp {
    public static int[] numbFreq(int[] array){
        int[] freq = new int[array.length];
        int visited = -1;
        for(int i = 0; i < array.length; i++){
            if(freq[i] != visited) {
                int count = 1;
                for(int j = i+1; j < array.length; j++){
                    if(array[i] == array[j]){
                        count++;
                    }
                    freq[j] = -1;
                }
                freq[i] = count
            }
        }
        return freq;
    }
}
