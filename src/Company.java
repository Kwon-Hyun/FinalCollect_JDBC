import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Company extends JFrame implements ActionListener {

    public Connection conn;
    public Statement s;
    public ResultSet r;

    private JComboBox Category;
    private JComboBox Dept;

    private JCheckBox c1 = new JCheckBox("Name", true);
    private JCheckBox c2 = new JCheckBox("Ssn", true);
    private JCheckBox c3 = new JCheckBox("Bdate", true);
    private JCheckBox c4 = new JCheckBox("Address", true);
    private JCheckBox c5 = new JCheckBox("Sex", true);
    private JCheckBox c6 = new JCheckBox("Salary", true);
    private JCheckBox c7 = new JCheckBox("Supervisor", true);
    private JCheckBox c8 = new JCheckBox("Department", true);
    private Vector<String> Head = new Vector<String>();

    private JTable table;
    private DefaultTableModel model;
    private static final int BOOLEAN_COLUMN = 0;
    private int NAME_COLUMN = 0;
    private int SSN_COLUMN = 0;
    private int BDATE_COLUMN = 0;
    private int ADDRESS_COLUMN = 0;
    private int SEX_COLUMN = 0;
    private int SALARY_COLUMN = 0;
    private int SUPERVISOR_COLUMN = 0;
    private int DEPARTMENT_COLUMN = 0;
    private String dShow;

    private JButton Search_Button = new JButton("검색");
    Container me = this;

    private JLabel totalEmp = new JLabel("인원수 : ");
    final JLabel totalCount = new JLabel();
    JPanel panel;
    JScrollPane ScPane;

    private JLabel Emplabel = new JLabel("선택한 직원: ");
    private JLabel ShowSelectedEmp = new JLabel();

    private JButton ShowEmpDepend_Button = new JButton("선택한 직원의 가족 보기");

    private JLabel EmpColumn = new JLabel("직원별 컬럼 수정: ");
    private JComboBox Update_column;

    //    private JLabel Setlabel = new JLabel("새로운 Salary: ");
//    private JTextField setSalary = new JTextField(10);
    private JTextField setColumn = new JTextField(10);
    private JButton Update_Button = new JButton("UPDATE");

    private JLabel deptSalary = new JLabel("부서별 Salary 수정: ");
    private JComboBox Update_salary;
    private JTextField setSalary = new JTextField(10);
    private JButton Update_Salary_Button = new JButton("SET SALARY");

    private JButton Delete_Button = new JButton("선택한 데이터 삭제");
    int count = 0;


    public Company() {
        JPanel ComboBoxPanel = new JPanel();
        String[] category = {"전체", "부서별"};
        String[] dept = {"Research", "Administration", "Headquarters"};
        Category = new JComboBox(category);
        Dept = new JComboBox(dept);
        ComboBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ComboBoxPanel.add(new JLabel("검색 범위 "));
        ComboBoxPanel.add(Category);
        ComboBoxPanel.add(Dept);

        JPanel CheckBoxPanel = new JPanel();
        CheckBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        CheckBoxPanel.add(new JLabel("검색 항목 "));
        CheckBoxPanel.add(c1);
        CheckBoxPanel.add(c2);
        CheckBoxPanel.add(c3);
        CheckBoxPanel.add(c4);
        CheckBoxPanel.add(c5);
        CheckBoxPanel.add(c6);
        CheckBoxPanel.add(c7);
        CheckBoxPanel.add(c8);
        CheckBoxPanel.add(Search_Button);

        JPanel ShowSelectedPanel = new JPanel();
        ShowSelectedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Emplabel.setFont(new Font("Dialog", Font.BOLD, 16));
        ShowSelectedEmp.setFont(new Font("Dialog", Font.BOLD, 16));
        dShow = "";
        ShowSelectedPanel.add(Emplabel);
        ShowSelectedPanel.add(ShowSelectedEmp);

        JPanel ShowEmpDependPanel = new JPanel();
        ShowEmpDependPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        ShowEmpDependPanel.add(ShowEmpDepend_Button);

        JPanel TotalPanel = new JPanel();
        TotalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        TotalPanel.add(totalEmp);
        TotalPanel.add(totalCount);

        JPanel UpdatePanel = new JPanel();
        String[] update_column = {"Fname", "Minit", "Lname", "Ssn", "Bdate", "Address", "Sex", "Salary", "Super_ssn", "Department"};
        Update_column = new JComboBox(update_column);
        UpdatePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        UpdatePanel.add(EmpColumn);
        UpdatePanel.add(Update_column);
        UpdatePanel.add(setColumn);
//        UpdatePanel.add(Setlabel);
        UpdatePanel.add(setSalary);
        UpdatePanel.add(Update_Button);

        JPanel DeletePanel = new JPanel();
        String[] update_salary = {"Research", "Administration", "Headquarters"};
        Update_salary = new JComboBox(update_salary);
        DeletePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        DeletePanel.add(deptSalary);
        DeletePanel.add(Update_salary);
        DeletePanel.add(setSalary);
        DeletePanel.add(Update_Salary_Button);

        Delete_Button.setForeground(new Color(255, 0, 0));
        DeletePanel.add(Delete_Button);

        JPanel Top = new JPanel();
        Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
        Top.add(ComboBoxPanel);
        Top.add(CheckBoxPanel);
        Top.add(ShowEmpDependPanel);

        JPanel Halfway = new JPanel();
        Halfway.setLayout(new BoxLayout(Halfway, BoxLayout.X_AXIS));
        Halfway.add(ShowSelectedPanel);

        JPanel Bottom = new JPanel();
        Bottom.setLayout(new BoxLayout(Bottom, BoxLayout.X_AXIS));
        Bottom.add(TotalPanel);
        Bottom.add(UpdatePanel);
        Bottom.add(DeletePanel);

        JPanel ShowVertical = new JPanel();
        ShowVertical.setLayout(new BoxLayout(ShowVertical, BoxLayout.Y_AXIS));
        ShowVertical.add(Halfway);
        ShowVertical.add(Bottom);

        getContentPane().add(Top, BorderLayout.NORTH);
        getContentPane().add(ShowVertical, BorderLayout.SOUTH);

        Search_Button.addActionListener(this);
        ShowEmpDepend_Button.addActionListener(this);
        Delete_Button.addActionListener(this);
        Update_Button.addActionListener(this);
        Update_Salary_Button.addActionListener(this);

        setTitle("Information Retrival System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        // DB연결
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC 드라이버 연결

            String user = "root";
            String pwd = "Mysql123!"; // 비밀번호 입력
            String dbname = "db";
            String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimezone=UTC";

            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("정상적으로 연결되었습니다 !");

        } catch (SQLException e1) {
            System.err.println("연결할 수 없습니다..");
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            System.err.println("드라이버를 로드할 수 없습니다.");
            e1.printStackTrace();
        }

        // ------------------------------------------------------------------------ //

        if (count == 1) {
            me.remove(panel);
            revalidate();
        }

        if (e.getSource() == Search_Button) {
            if (c1.isSelected() || c2.isSelected() || c3.isSelected() || c4.isSelected() || c5.isSelected()
                    || c6.isSelected() || c7.isSelected() || c8.isSelected()) {
                Head.clear();
                Head.add("선택");

                String stmt = "select";
                if (c1.isSelected()) {
                    stmt += " concat(e.fname,' ', e.minit,' ', e.lname,' ') as Name";
                    Head.add("NAME");
                }
                if (c2.isSelected()) {
                    if (!c1.isSelected())
                        stmt += " e.ssn";
                    else
                        stmt += ", e.ssn";
                    Head.add("SSN");
                }
                if (c3.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected())
                        stmt += " e.bdate";
                    else
                        stmt += ", e.bdate";
                    Head.add("BDATE");
                }
                if (c4.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected())
                        stmt += " e.address";
                    else
                        stmt += ", e.address";
                    Head.add("ADDRESS");
                }
                if (c5.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected())
                        stmt += " e.sex";
                    else
                        stmt += ", e.sex";
                    Head.add("SEX");
                }
                if (c6.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected()
                            && !c5.isSelected())
                        stmt += " e.salary";
                    else
                        stmt += ", e.salary";
                    Head.add("SALARY");
                }
                if (c7.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected() && !c5.isSelected()
                            && !c6.isSelected())
                        stmt += " concat(s.fname, ' ', s.minit, ' ',s.lname,' ') as Supervisor ";
                    else
                        stmt += ", concat(s.fname, ' ', s.minit, ' ',s.lname,' ') as Supervisor ";
                    Head.add("SUPERVISOR");
                }
                if (c8.isSelected()) {
                    if (!c1.isSelected() && !c2.isSelected() && !c3.isSelected() && !c4.isSelected() && !c5.isSelected()
                            && !c6.isSelected() && !c7.isSelected())
                        stmt += " dname";
                    else
                        stmt += ", dname";
                    Head.add("DEPARTMENT");
                }
                // Left outer join을 하는 이유는 Supervisor가 없는 경우(NULL)도 표시하기 위함.
                stmt += " from EMPLOYEE e left outer join EMPLOYEE s on e.super_ssn=s.ssn, DEPARTMENT where e.dno = dnumber";

                if (Category.getSelectedItem().toString() == "부서별") {
                    if (Dept.getSelectedItem().toString() == "Research")
                        stmt += " and dname = \"Research\";";
                    else if (Dept.getSelectedItem().toString() == "Administration")
                        stmt += " and dname = \"Administration\";";
                    else if (Dept.getSelectedItem().toString() == "Headquarters")
                        stmt += " and dname = \"Headquarters\";";
                }

                model = new DefaultTableModel(Head, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        if (column > 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                };
                for (int i = 0; i < Head.size(); i++) {
                    if (Head.get(i) == "NAME") {
                        NAME_COLUMN = i;
                    } else if (Head.get(i) == "SSN") {
                        SSN_COLUMN = i;
                    } else if (Head.get(i) == "BDATE") {
                        BDATE_COLUMN = i;
                    } else if (Head.get(i) == "ADDRESS") {
                        ADDRESS_COLUMN = i;
                    } else if (Head.get(i) == "SEX") {
                        SEX_COLUMN = i;
                    } else if (Head.get(i) == "SALARY") {
                        SALARY_COLUMN = i;
                    } else if (Head.get(i) == "SUPERVISOR") {
                        SUPERVISOR_COLUMN = i;
                    } else if (Head.get(i) == "DEPARTMENT") {
                        DEPARTMENT_COLUMN = i;
                    }
                }
                table = new JTable(model) {
                    @Override
                    public Class getColumnClass(int column) {
                        if (column == 0) {
                            return Boolean.class;
                        } else
                            return String.class;
                    }
                };

                ShowSelectedEmp.setText(" ");

                try {

                    count = 1;
                    s = conn.createStatement();
                    r = s.executeQuery(stmt);
                    ResultSetMetaData rsmd = r.getMetaData();
                    int columnCnt = rsmd.getColumnCount();
                    int rowCnt = table.getRowCount();

                    while (r.next()) {
                        Vector<Object> tuple = new Vector<Object>();
                        tuple.add(false);
                        for (int i = 1; i < columnCnt + 1; i++) {
                            tuple.add(r.getString(rsmd.getColumnName(i)));
                        }
                        model.addRow(tuple);
                        rowCnt++;
                    }
                    totalCount.setText(String.valueOf(rowCnt));

                } catch (SQLException ee) {
                    System.out.println("actionPerformed err : " + ee);
                    ee.printStackTrace();

                }
                panel = new JPanel();
                ScPane = new JScrollPane(table);
                table.getModel().addTableModelListener(new CheckBoxModelListener());
                ScPane.setPreferredSize(new Dimension(1100, 400));
                panel.add(ScPane);
                getContentPane().add(panel, BorderLayout.CENTER);
                revalidate();

            } else {
                JOptionPane.showMessageDialog(null, "검색 항목을 한 개 이상 선택하세요.");
            }
        }

        // 선택한 직원의 가족 보기
        if (e.getSource() == ShowEmpDepend_Button) {
            Vector<String> ShowEmpDependent = new Vector<String>();
            String showEmpDependentStmt = "";
            try {
                count = 1;
                String columnName = model.getColumnName(2);
                if (columnName == "SSN") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            ShowEmpDependent.add((String) table.getValueAt(i, 2));
                        }
                    }

                    for (int i = 0; i < ShowEmpDependent.size(); i++) {
                        for (int k = 0; k < model.getRowCount(); k++) {
                            if (table.getValueAt(k, 0) == Boolean.TRUE) {
                                model.removeRow(k);
                                totalCount.setText(String.valueOf(table.getRowCount()));
                            }
                        }
                    }
                    model.setNumRows(0);
                    for (int i = 0; i < ShowEmpDependent.size(); i++) {
                        showEmpDependentStmt = "SELECT * FROM DEPENDENT WHERE Essn=?";
                        PreparedStatement p = conn.prepareStatement(showEmpDependentStmt);
                        p.clearParameters();
                        p.setString(1, String.valueOf(ShowEmpDependent.get(i)));
                        s = conn.createStatement();
                        r = p.executeQuery();

                        ResultSetMetaData rsmd2 = r.getMetaData();
                        int columnCnt = rsmd2.getColumnCount();
                        int rowCnt = table.getRowCount();

                        while (r.next()) {
                            Vector<Object> tuple = new Vector<Object>();
                            tuple.add(false);
                            for (int j = 1; j < columnCnt + 1; j++) {
                                tuple.add(r.getString(rsmd2.getColumnName(j)));
                            }
                            model.addRow(tuple);
                            rowCnt++;
                        }
                        totalCount.setText(String.valueOf(rowCnt));
                    }
                }
            } catch (SQLException ee) {
                System.out.println("actionPerformed err : " + ee);
                ee.printStackTrace();
            }
            panel = new JPanel();
            ScPane = new JScrollPane(table);
            table.getModel().addTableModelListener(new CheckBoxModelListener());
            ScPane.setPreferredSize(new Dimension(1100, 400));
            panel.add(ScPane);
            add(panel, BorderLayout.CENTER);
            revalidate();

        } else if (model.getColumnName(2) != "SSN") {
            JOptionPane.showMessageDialog(null, "가족 검색을 위해서는 NAME, SSN 항목을 모두 체크해주세요.");
        }
        // 선택한 직원의 가족 보기 끝

        // UPDATE_SALARY
        if (e.getSource() == Update_Salary_Button) {
            try {
                if (setSalary.getText().isEmpty()) {
                    throw new Exception();
                } else if (Update_salary.getSelectedItem().toString() == "Research") {
                    String updateStmt = "UPDATE EMPLOYEE SET Salary=? WHERE Dno=?";
                    PreparedStatement p = conn.prepareStatement(updateStmt);
                    p.clearParameters();
                    String updateSalary = setSalary.getText();
                    p.setString(1, updateSalary);
                    p.setString(2, "5");
                    p.executeUpdate();
                } else if (Update_salary.getSelectedItem().toString() == "Administration") {
                    String updateStmt = "UPDATE EMPLOYEE SET Salary=? WHERE Dno=?";
                    PreparedStatement p = conn.prepareStatement(updateStmt);
                    p.clearParameters();
                    String updateSalary = setSalary.getText();
                    p.setString(1, updateSalary);
                    p.setString(2, "4");
                    p.executeUpdate();
                } else if (Update_salary.getSelectedItem().toString() == "Headquaters") {
                    String updateStmt = "UPDATE EMPLOYEE SET Salary=? WHERE Dno=?";
                    PreparedStatement p = conn.prepareStatement(updateStmt);
                    p.clearParameters();
                    String updateSalary = setSalary.getText();
                    p.setString(1, updateSalary);
                    p.setString(2, "1");
                    p.executeUpdate();
                }
            } catch (SQLException e1) {
                System.out.println("actionPerformed err : " + e1);
                e1.printStackTrace();
            } catch (Exception e1) {
                String errMsg = e1.toString();
                if (errMsg.equals("java.lang.Exception")) {
                    JOptionPane.showMessageDialog(null, "형식에 맞게 입력해주세요.");
                } else {
                    JOptionPane.showMessageDialog(null, errMsg.substring(20));
                }
            }
        } // UPDATE_SALARY 끝

        // DELETE
        if (e.getSource() == Delete_Button) {
            Vector<String> delete_ssn = new Vector<String>();

            try {
                String columnName = model.getColumnName(2);
                if (columnName == "SSN") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            delete_ssn.add((String) table.getValueAt(i, 2));
                        }
                    }
                    for (int i = 0; i < delete_ssn.size(); i++) {
                        for (int k = 0; k < model.getRowCount(); k++) {
                            if (table.getValueAt(k, 0) == Boolean.TRUE) {
                                model.removeRow(k);
                                totalCount.setText(String.valueOf(table.getRowCount()));
                            }
                        }
                    }
                    for (int i = 0; i < delete_ssn.size(); i++) {
                        String deleteStmt = "DELETE FROM EMPLOYEE WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(deleteStmt);
                        p.clearParameters();
                        p.setString(1, String.valueOf(delete_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "삭제 작업을 진행하시려면 NAME, SSN 항목을 모두 체크해주세요.");
                }

                ShowSelectedEmp.setText(" ");

            } catch (SQLException e1) {
                System.out.println("actionPerformed err : " + e1);
                e1.printStackTrace();
            }
            panel = new JPanel();
            ScPane = new JScrollPane(table);
            ScPane.setPreferredSize(new Dimension(1100, 400));
            panel.add(ScPane);
            getContentPane().add(panel, BorderLayout.CENTER);
            revalidate();

        } // DELETE 끝

        // UPDATE
        if (e.getSource() == Update_Button) {
            Vector<String> update_ssn = new Vector<String>();
            try {
                if (setColumn.getText().isEmpty()) {
                    throw new Exception();
                } else if (Update_column.getSelectedItem().toString() == "Fname") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String Name1 = (String) table.getValueAt(i, 1);
                            String word1 = Name1.split(" ")[1];
                            String word2 = Name1.split(" ")[2];
                            String updateColumn = setColumn.getText();
                            table.setValueAt(updateColumn + " " + word1 + " " + word2, i, NAME_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Fname=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Minit") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String Name1 = (String) table.getValueAt(i, 1);
                            String word1 = Name1.split(" ")[0];
                            String word2 = Name1.split(" ")[2];
                            String updateColumn = setColumn.getText();
                            table.setValueAt(word1 + " " + updateColumn + " " + word2, i, NAME_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Minit=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Lname") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String Name1 = (String) table.getValueAt(i, 1);
                            String word1 = Name1.split(" ")[0];
                            String word2 = Name1.split(" ")[1];
                            String updateColumn = setColumn.getText();
                            table.setValueAt(word1 + " " + word2 + " " + updateColumn, i, NAME_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Lname=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Ssn") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            Integer updateColumn = Integer.parseInt(setColumn.getText());
                            table.setValueAt(updateColumn, i, SSN_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Ssn=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Bdate") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            table.setValueAt(updateColumn, i, BDATE_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Bdate=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Address") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            table.setValueAt(updateColumn, i, ADDRESS_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Address=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Sex") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            table.setValueAt(updateColumn, i, SEX_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Sex=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Salary") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            table.setValueAt(Double.parseDouble(updateColumn), i, SALARY_COLUMN);
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Salary=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Super_ssn") {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            Boolean flag = Boolean.FALSE;
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            for (int j = 0; j < table.getRowCount(); j++) {
                                if (updateColumn.equals((String) table.getValueAt(j, 2))) { //updateColumn 에는 상사의 Ssn이 들어옴
                                    table.setValueAt((String) table.getValueAt(j, 1), i, SUPERVISOR_COLUMN);
                                    flag = Boolean.TRUE;
                                }
                            }
                            if (flag == Boolean.FALSE) {
                                throw new Exception("존재하는 직원을 입력해주세요.");
                            }
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Super_ssn=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } else if (Update_column.getSelectedItem().toString() == "Department") { //Dno로 바꿔서 할 것
                    String Dno5 = "Research";
                    String Dno4 = "Administration";
                    String Dno1 = "Headquaters";
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0) == Boolean.TRUE) {
                            update_ssn.add((String) table.getValueAt(i, 2));
                            String updateColumn = setColumn.getText();
                            if (updateColumn.equals(Dno5) || updateColumn.equals(Dno4) || updateColumn.equals(Dno1)) {
                                table.setValueAt(updateColumn, i, DEPARTMENT_COLUMN);
                            } else {
                                throw new Exception("Research, Administration, Headquaters 세 부서만 입력해주세요.");
                            }
                        }
                    }
                    for (int i = 0; i < update_ssn.size(); i++) {
                        String updateStmt = "UPDATE EMPLOYEE SET Dno=? WHERE Ssn=?";
                        PreparedStatement p = conn.prepareStatement(updateStmt);
                        p.clearParameters();
                        String updateColumn = setColumn.getText();

                        if (updateColumn.equals(Dno5)) {
                            updateColumn = "5";
                        } else if (updateColumn.equals(Dno4)) {
                            updateColumn = "4";
                        } else if (updateColumn.equals(Dno1)) {
                            updateColumn = "1";
                        }
                        p.setString(1, updateColumn);
                        p.setString(2, String.valueOf(update_ssn.get(i)));
                        p.executeUpdate();
                    }
                } //else {
                //JOptionPane.showMessageDialog(null, "수정 작업을 진행하시려면 검색 항목을 모두 체크해주세요.");
                //}

                ShowSelectedEmp.setText(" ");

            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(null, "숫자 형식에 맞게 입력해주세요.");
            } catch (SQLException e1) {
                System.out.println("actionPerformed err : " + e1);
                e1.printStackTrace();
            } catch (Exception e1) {
                String errMsg = e1.toString();
                if (errMsg.equals("java.lang.Exception")) {
                    JOptionPane.showMessageDialog(null, "형식에 맞게 입력해주세요.");
                } else {
                    JOptionPane.showMessageDialog(null, errMsg.substring(20));
                }
            }
            panel = new JPanel();
            ScPane = new JScrollPane(table);
            ScPane.setPreferredSize(new Dimension(1100, 400));
            panel.add(ScPane);
            getContentPane().add(panel, BorderLayout.CENTER);
            revalidate();

        } // UPDATE 끝
    }

    public class CheckBoxModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == BOOLEAN_COLUMN) {
                TableModel model = (TableModel) e.getSource();
                String columnName = model.getColumnName(1);
                Boolean checked = (Boolean) model.getValueAt(row, column);
                if (columnName == "NAME") {
                    if (checked) {
                        dShow = "";
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                dShow += (String) table.getValueAt(i, NAME_COLUMN) + "    ";
                            }
                        }
                        ShowSelectedEmp.setText(dShow);
                    } else {
                        dShow = "";
                        for (int i = 0; i < table.getRowCount(); i++) {
                            if (table.getValueAt(i, 0) == Boolean.TRUE) {
                                dShow += (String) table.getValueAt(i, 1) + "    ";
                            }
                        }
                        ShowSelectedEmp.setText(dShow);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Company();
    }
}