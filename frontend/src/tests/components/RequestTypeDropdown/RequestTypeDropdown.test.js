import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { requestTypeFixtures } from 'fixtures/requestFixtures';
import RequestTypeDropdown from '../../../main/components/RequestTypeDropdown/RequestTypeDropdown';

describe('RequestTypeDropdown Component', () => {
  const options = [
    { value: 'Scholarship or Fellowship', label: 'POST Request' },
    { value: 'MS program (other than CS Dept BS/MS)', label: 'PUT Request' },
    { value: 'PhD program', label: 'DELETE Request' },
  ];

  test('renders without crashing', () => {
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} onChange={() => {}} />);
    const dropdown = screen.getByRole('combobox');
    expect(dropdown).toBeInTheDocument();
  });

  test('renders all options correctly', () => {
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} onChange={() => {}} />);
    fireEvent.mouseDown(screen.getByRole('combobox'));
    options.forEach(option => {
      expect(screen.getByText(option.value)).toBeInTheDocument();
    });
  });

  test('calls onChange when an option is selected', () => {
    const handleChange = jest.fn()
    
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} onChange={handleChange} />);
    const dropdown = screen.getByRole('combobox');
    fireEvent.mouseDown(dropdown);
    fireEvent.click(screen.getByText('PhD program'));
    expect(handleChange).toHaveBeenCalledWith('PhD program');
  });

  test('displays the correct selected value', () => {
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} value="PUT" onChange={() => {}} />);
    const dropdown = screen.getByRole('combobox');
    expect(dropdown.value).toBe('Scholarship or Fellowship');
  });
});
