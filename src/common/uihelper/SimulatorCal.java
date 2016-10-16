package common.uihelper;

public class SimulatorCal
{
    private String content = "";
    private double result = 0.0d;
    private int CLEAR_STATE = 0;
    private int NUMBER_STATE = 1;
    private int QUOTE_STATE = 2;

    private int state = 0;

    public SimulatorCal()
    {
        clearContent();
        this.state = this.CLEAR_STATE;
    }

    public SimulatorCal(String value)
    {
        this();
        if (Double.parseDouble(value) > 0)
        {
            for (int i = 0; i < value.length(); i++)
            {
                simulate(value.substring(i, i + 1));
            }
        }
    }

    public void simulate(String keycode)
    {
        System.out.println("keycode: " + keycode);
        if (keycode != null)
        {
            if (keycode.matches("[\\.0-9]+"))
            {
                append(keycode);
                this.state = this.NUMBER_STATE;
            }
            else if (this.state == this.NUMBER_STATE
                    && keycode.matches("[+-]+"))
            {
                append(" " + keycode + " ");
                this.state = this.QUOTE_STATE;
            }
            else if (keycode.equals("c"))
            {
                clearContent();
                this.state = this.CLEAR_STATE;
            }
            else if (keycode.equals("="))
            {
                if (this.state != this.QUOTE_STATE)
                {
                    calulateContent();
                }
                else
                {
                    // throw new RuntimeException("末尾是符号,无法计算");
                }
            }
        }
    }

    private void calulateContent()
    {
        this.result = 0d;

        String[] parts = this.content.split(" ");
        // System.out.println("��������: " + Arrays.toString(parts));
        if (parts.length == 1)
        {
            this.result = Double.parseDouble(parts[0]);
        }
        else if (parts.length > 1)
        {
            int index = 1;
            double left, right;
            this.result = Double.parseDouble(parts[0]);
            while (index < parts.length - 1)
            {
                left = this.result;
                right = Double.parseDouble(parts[index + 1]);
                if (parts[index].equals("+"))
                {
                    this.result = left + right;
                }
                else
                {
                    this.result = left - right;
                }
                index += 2;
            }
        }
        this.content = this.result + "";
        // System.out.println("###result:" + this.result);
    }

    private void clearContent()
    {
        // System.out.println("���" + this.content);
        this.content = "0";
    }

    private void append(String str)
    {
        if (this.state == this.CLEAR_STATE)
        {
            this.content = str;
        }
        else
        {
            this.content += str;
        }
        // System.out.println("now: " + this.content);
    }

    public String getContent()
    {
        return this.content;
    }

    public double getResult()
    {
        return this.result;
    }

}
